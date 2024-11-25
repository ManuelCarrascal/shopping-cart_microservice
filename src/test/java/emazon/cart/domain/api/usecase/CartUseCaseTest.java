package emazon.cart.domain.api.usecase;

import emazon.cart.domain.exception.CategoriesLimitException;
import emazon.cart.domain.exception.InsufficientStockException;
import emazon.cart.domain.exception.NotFoundException;
import emazon.cart.domain.model.Cart;
import emazon.cart.domain.model.Pagination;
import emazon.cart.domain.spi.IAuthenticationPersistencePort;
import emazon.cart.domain.spi.ICartPersistencePort;
import emazon.cart.domain.spi.IStockConnectionPersistencePort;
import emazon.cart.domain.spi.ISupplyConnectionPersistencePort;
import emazon.cart.domain.util.CartUseCaseConstants;
import emazon.cart.ports.application.http.dto.product.ProductResponse;
import emazon.cart.ports.application.http.dto.product.ProductCartRequest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class CartUseCaseTest {

    private final IAuthenticationPersistencePort authenticationPersistencePortMock = mock(IAuthenticationPersistencePort.class, "authenticationPersistencePort");
    private final IStockConnectionPersistencePort stockConnectionPersistencePortMock = mock(IStockConnectionPersistencePort.class, "stockConnectionPersistencePort");
    private final ISupplyConnectionPersistencePort supplyConnectionPersistencePortMock = mock(ISupplyConnectionPersistencePort.class, "supplyConnectionPersistencePort");
    private final ICartPersistencePort cartPersistencePortMock = mock(ICartPersistencePort.class, "cartPersistencePort");

    @Test
    void addProductToCartWhenProductNotExistThrowsNotFoundException() {
        doReturn(1L).when(authenticationPersistencePortMock).getAuthenticatedUserId();
        doReturn(false).when(stockConnectionPersistencePortMock).existById(1L); // Producto no existe
        CartUseCase target = new CartUseCase(cartPersistencePortMock, authenticationPersistencePortMock, stockConnectionPersistencePortMock, supplyConnectionPersistencePortMock);
        Cart cart = new Cart();
        cart.setProductId(1L);
        cart.setUserId(1L);

        final NotFoundException result = assertThrows(NotFoundException.class, () -> target.addProductToCart(cart));

        assertAll("result", () -> {
            assertThat(result.getMessage(), equalTo(CartUseCaseConstants.PRODUCT_NOT_FOUND));
            verify(stockConnectionPersistencePortMock).existById(1L);
        });
    }
    @Test
    void addProductToCart_WithNonExistentProductId_ShouldThrowNotFoundException() {
        doReturn(0L).when(authenticationPersistencePortMock).getAuthenticatedUserId();
        doReturn(false).when(stockConnectionPersistencePortMock).existById(0L);
        CartUseCase target = new CartUseCase(cartPersistencePortMock, authenticationPersistencePortMock, stockConnectionPersistencePortMock, supplyConnectionPersistencePortMock);
        Cart cart = new Cart();
        cart.setProductId(0L);
        cart.setUserId(0L);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> target.addProductToCart(cart));
        assertThat(exception.getMessage(), equalTo("Product not found"));
        verify(authenticationPersistencePortMock).getAuthenticatedUserId();
        verify(stockConnectionPersistencePortMock).existById(0L);
    }



    @Test
    void addProductToCartWhenStockIsInsufficientThrowsInsufficientStockException() {

        doReturn(2L).when(authenticationPersistencePortMock).getAuthenticatedUserId();
        doReturn(true).when(stockConnectionPersistencePortMock).existById(2L);
        doReturn(false).when(stockConnectionPersistencePortMock).isStockSufficient(2L, 1);
        doReturn("2024-01-01T10:15:30").when(supplyConnectionPersistencePortMock).getNextSupplyDate(2L);

        CartUseCase target = new CartUseCase(cartPersistencePortMock, authenticationPersistencePortMock, stockConnectionPersistencePortMock, supplyConnectionPersistencePortMock);

        Cart cart = new Cart();
        cart.setProductId(2L);
        cart.setUserId(2L);
        cart.setQuantity(1);

        final InsufficientStockException result = assertThrows(InsufficientStockException.class, () -> target.addProductToCart(cart));

        assertAll("result", () -> {
            assertThat(result.getMessage(), equalTo(CartUseCaseConstants.INSUFFICIENT_STOCK));
            assertThat(result.getNextSupplyDate(), equalTo("2024-01-01T10:15:30"));
            verify(stockConnectionPersistencePortMock).existById(2L);
            verify(stockConnectionPersistencePortMock).isStockSufficient(2L, 1);
            verify(supplyConnectionPersistencePortMock).getNextSupplyDate(2L);
        });
    }



    @Test
    void addProductToCartWhenExistingCartIsUpdated() {
        doReturn(3L).when(authenticationPersistencePortMock).getAuthenticatedUserId();
        doReturn(true).when(stockConnectionPersistencePortMock).existById(3L);
        doReturn(true).when(stockConnectionPersistencePortMock).isStockSufficient(3L, 1);
        Cart cartMock = mock(Cart.class);
        doReturn(cartMock).when(cartPersistencePortMock).findProductByUserIdAndProductId(3L, 3L);
        doReturn(1).when(cartMock).getQuantity();
        doNothing().when(cartMock).setQuantity(2);
        doNothing().when(cartMock).setUpdatedAt(any());
        Date date = new Date();
        doReturn(date).when(cartMock).getCreatedAt();
        Date date2 = new Date();
        doReturn(date2).when(cartMock).getUpdatedAt();
        doNothing().when(cartPersistencePortMock).addProductToCart(cartMock);
        CartUseCase target = new CartUseCase(cartPersistencePortMock, authenticationPersistencePortMock, stockConnectionPersistencePortMock, supplyConnectionPersistencePortMock);
        Cart cart = new Cart();
        cart.setCreatedAt(date);
        cart.setQuantity(1);
        cart.setProductId(3L);
        cart.setUserId(3L);
        cart.setUpdatedAt(date2);

        target.addProductToCart(cart);

        assertAll("result", () -> {
            verify(stockConnectionPersistencePortMock).existById(3L);
            verify(stockConnectionPersistencePortMock).isStockSufficient(3L, 1);
            verify(cartPersistencePortMock).findProductByUserIdAndProductId(3L, 3L);
            verify(cartMock).getQuantity();
            verify(cartMock).setQuantity(2);
            verify(cartMock).setUpdatedAt(any());
            verify(cartMock).getCreatedAt();
            verify(cartMock).getUpdatedAt();
            verify(cartPersistencePortMock).addProductToCart(cartMock);
        });
    }

    @Test
    void removeProductToCartWhenProductExistsRemovesSuccessfully() {
        Long userId = 1L;
        Long productId = 1L;

        Cart existingCart = new Cart();
        doReturn(existingCart).when(cartPersistencePortMock).findProductByUserIdAndProductId(userId, productId);
        doNothing().when(cartPersistencePortMock).removeProductFromCart(userId, productId);
        doNothing().when(cartPersistencePortMock).updateCartItemsUpdatedAt(eq(userId), any());

        CartUseCase target = new CartUseCase(cartPersistencePortMock, authenticationPersistencePortMock, stockConnectionPersistencePortMock, supplyConnectionPersistencePortMock);

        target.removeProductToCart(userId, productId);

        assertAll("result", () -> {
            verify(cartPersistencePortMock).findProductByUserIdAndProductId(userId, productId);
            verify(cartPersistencePortMock).removeProductFromCart(userId, productId);
            verify(cartPersistencePortMock).updateCartItemsUpdatedAt(eq(userId), any());
        });
    }

    @Test
    void removeProductToCartWhenProductNotExistThrowsNotFoundException() {
        Long userId = 1L;
        Long productId = 1L;

        doReturn(null).when(cartPersistencePortMock).findProductByUserIdAndProductId(userId, productId);

        CartUseCase target = new CartUseCase(cartPersistencePortMock, authenticationPersistencePortMock, stockConnectionPersistencePortMock, supplyConnectionPersistencePortMock);

        final NotFoundException result = assertThrows(NotFoundException.class, () -> target.removeProductToCart(userId, productId));

        assertAll("result", () -> {
            assertThat(result.getMessage(), equalTo(CartUseCaseConstants.PRODUCT_NOT_FOUND));
            verify(cartPersistencePortMock).findProductByUserIdAndProductId(userId, productId);
        });
    }

   @Test
    void createNewCartTest() throws Exception {
        Cart cart = new Cart();
        CartUseCase target = new CartUseCase(cartPersistencePortMock, authenticationPersistencePortMock, stockConnectionPersistencePortMock, supplyConnectionPersistencePortMock);

        Method createNewCartMethod = CartUseCase.class.getDeclaredMethod("createNewCart", Cart.class);
        createNewCartMethod.setAccessible(true);
        createNewCartMethod.invoke(target, cart);

        assertAll("createNewCart", () -> {
            assertThat(cart.getCreatedAt(), is(notNullValue()));
            assertThat(cart.getUpdatedAt(), is(notNullValue()));
            verify(cartPersistencePortMock).addProductToCart(cart);
        });
    }

    @Test
    void addProductToCartWhenNewCartIsCreated() {
        Long userId = 6L;
        Long productId = 6L;

        doReturn(userId).when(authenticationPersistencePortMock).getAuthenticatedUserId();
        doReturn(true).when(stockConnectionPersistencePortMock).existById(productId);
        doReturn(true).when(stockConnectionPersistencePortMock).isStockSufficient(productId, 1);

        List<Long> productIds = new ArrayList<>();
        doReturn(productIds).when(cartPersistencePortMock).findProductIdsByUserId(userId);

        doReturn(Arrays.asList("Electronics", "Books")).when(stockConnectionPersistencePortMock).getCategoryNamesByProductId(any());

        CartUseCase target = new CartUseCase(cartPersistencePortMock, authenticationPersistencePortMock, stockConnectionPersistencePortMock, supplyConnectionPersistencePortMock);
        Cart cart = new Cart();
        cart.setProductId(productId);
        cart.setUserId(userId);
        cart.setQuantity(1);

        target.addProductToCart(cart);

        assertAll("result", () -> {
            verify(stockConnectionPersistencePortMock).existById(productId);
            verify(stockConnectionPersistencePortMock).isStockSufficient(productId, 1);
            verify(cartPersistencePortMock).findProductIdsByUserId(userId);
            verify(stockConnectionPersistencePortMock).getCategoryNamesByProductId(any());
            verify(cartPersistencePortMock).addProductToCart(any(Cart.class));
        });
    }

    @Test
    void createProductCartRequestTest() throws Exception {
        Long userId = 1L;
        List<Long> productIds = Arrays.asList(1L, 2L);
        doReturn(productIds).when(cartPersistencePortMock).findProductIdsByUserId(userId);

        CartUseCase target = new CartUseCase(cartPersistencePortMock, authenticationPersistencePortMock, stockConnectionPersistencePortMock, supplyConnectionPersistencePortMock);

        Method method = CartUseCase.class.getDeclaredMethod("createProductCartRequest", Long.class);
        method.setAccessible(true);
        ProductCartRequest result = (ProductCartRequest) method.invoke(target, userId);

        assertAll("result", () -> {
            assertThat(result.getProductIds(), is(productIds));
            verify(cartPersistencePortMock).findProductIdsByUserId(userId);
        });
    }

    @Test
    void calculateTotalForAllItemsTest() throws Exception {
        Long userId = 1L;
        List<Long> productIds = Arrays.asList(1L, 2L);
        doReturn(productIds).when(cartPersistencePortMock).findProductIdsByUserId(userId);
        Cart cart1 = new Cart();
        cart1.setQuantity(2);
        Cart cart2 = new Cart();
        cart2.setQuantity(3);
        doReturn(cart1).when(cartPersistencePortMock).findProductByUserIdAndProductId(userId, 1L);
        doReturn(cart2).when(cartPersistencePortMock).findProductByUserIdAndProductId(userId, 2L);
        doReturn(50.0).when(stockConnectionPersistencePortMock).getProductPriceById(1L);
        doReturn(30.0).when(stockConnectionPersistencePortMock).getProductPriceById(2L);

        CartUseCase target = new CartUseCase(cartPersistencePortMock, authenticationPersistencePortMock, stockConnectionPersistencePortMock, supplyConnectionPersistencePortMock);

        Method method = CartUseCase.class.getDeclaredMethod("calculateTotalForAllItems", Long.class);
        method.setAccessible(true);
        double result = (double) method.invoke(target, userId);

        assertAll("result", () -> {
            assertThat(result, is(190.0));
            verify(cartPersistencePortMock).findProductIdsByUserId(userId);
            verify(cartPersistencePortMock).findProductByUserIdAndProductId(userId, 1L);
            verify(cartPersistencePortMock).findProductByUserIdAndProductId(userId, 2L);
            verify(stockConnectionPersistencePortMock).getProductPriceById(1L);
            verify(stockConnectionPersistencePortMock).getProductPriceById(2L);
        });
    }

    @Test
    void updateProductDetailsInCartTest() throws Exception {
        Long userId = 1L;
        ProductResponse productResponse1 = new ProductResponse();
        productResponse1.setProductId(1L);
        productResponse1.setProductPrice(50.0);
        productResponse1.setProductQuantity(5);
        ProductResponse productResponse2 = new ProductResponse();
        productResponse2.setProductId(2L);
        productResponse2.setProductPrice(30.0);
        productResponse2.setProductQuantity(2);
        List<ProductResponse> productResponses = Arrays.asList(productResponse1, productResponse2);
        Pagination<ProductResponse> productResponsePagination = new Pagination<>(true, 0, 10, 2L, productResponses, 0.0);

        Cart cart1 = new Cart();
        cart1.setQuantity(2);
        Cart cart2 = new Cart();
        cart2.setQuantity(3);
        doReturn(cart1).when(cartPersistencePortMock).findProductByUserIdAndProductId(userId, 1L);
        doReturn(cart2).when(cartPersistencePortMock).findProductByUserIdAndProductId(userId, 2L);
        doReturn("2024-01-01T10:15:30").when(supplyConnectionPersistencePortMock).getNextSupplyDate(2L);

        CartUseCase target = new CartUseCase(cartPersistencePortMock, authenticationPersistencePortMock, stockConnectionPersistencePortMock, supplyConnectionPersistencePortMock);

        Method method = CartUseCase.class.getDeclaredMethod("updateProductDetailsInCart", Long.class, Pagination.class);
        method.setAccessible(true);
        method.invoke(target, userId, productResponsePagination);

        assertAll("result", () -> {
            assertThat(productResponse1.getSubtotal(), is(100.0));
            assertThat(productResponse1.getCartQuantity(), is(2));
            assertThat(productResponse2.getSubtotal(), is(90.0));
            assertThat(productResponse2.getCartQuantity(), is(3));
            assertThat(productResponse2.getNextSupplyDate(), is("2024-01-01T10:15:30"));
            verify(cartPersistencePortMock).findProductByUserIdAndProductId(userId, 1L);
            verify(cartPersistencePortMock).findProductByUserIdAndProductId(userId, 2L);
            verify(supplyConnectionPersistencePortMock).getNextSupplyDate(2L);
        });
    }
    @Test
    void checkCategoriesLimitTest() throws Exception {
        List<Long> productIds = Arrays.asList(1L, 2L, 3L, 4L);
        List<String> categories1 = Arrays.asList("Electronics", "Books");
        List<String> categories2 = Arrays.asList("Electronics", "Toys");
        List<String> categories3 = List.of("Electronics");
        List<String> categories4 = List.of("Electronics");

        doReturn(categories1).when(stockConnectionPersistencePortMock).getCategoryNamesByProductId(1L);
        doReturn(categories2).when(stockConnectionPersistencePortMock).getCategoryNamesByProductId(2L);
        doReturn(categories3).when(stockConnectionPersistencePortMock).getCategoryNamesByProductId(3L);
        doReturn(categories4).when(stockConnectionPersistencePortMock).getCategoryNamesByProductId(4L);

        CartUseCase target = new CartUseCase(cartPersistencePortMock, authenticationPersistencePortMock, stockConnectionPersistencePortMock, supplyConnectionPersistencePortMock);

        Method checkCategoriesLimitMethod = CartUseCase.class.getDeclaredMethod("checkCategoriesLimit", List.class);
        checkCategoriesLimitMethod.setAccessible(true);

        CategoriesLimitException exception = assertThrows(CategoriesLimitException.class, () -> invokeCheckCategoriesLimit(target, checkCategoriesLimitMethod, productIds));

        assertThat(exception.getMessage(), containsString("Electronics"));
        verify(stockConnectionPersistencePortMock).getCategoryNamesByProductId(1L);
        verify(stockConnectionPersistencePortMock).getCategoryNamesByProductId(2L);
        verify(stockConnectionPersistencePortMock).getCategoryNamesByProductId(3L);
        verify(stockConnectionPersistencePortMock).getCategoryNamesByProductId(4L);
    }

    private void invokeCheckCategoriesLimit(CartUseCase target, Method method, List<Long> productIds) throws Exception {
        try {
            method.invoke(target, productIds);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof CategoriesLimitException) {
                throw (CategoriesLimitException) e.getCause();
            } else {
                throw e;
            }
        }
    }


}
