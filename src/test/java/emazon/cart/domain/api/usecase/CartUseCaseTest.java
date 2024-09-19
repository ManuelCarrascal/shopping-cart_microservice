package emazon.cart.domain.api.usecase;

import emazon.cart.domain.exception.CategoriesLimitException;
import emazon.cart.domain.exception.InsufficientStockException;
import emazon.cart.domain.exception.NotFoundException;
import emazon.cart.domain.model.Cart;
import emazon.cart.domain.spi.IAuthenticationPersistencePort;
import emazon.cart.domain.spi.ICartPersistencePort;
import emazon.cart.domain.spi.IStockConnectionPersistencePort;
import emazon.cart.domain.spi.ISupplyConnectionPersistencePort;
import emazon.cart.domain.util.CartUseCaseConstants;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doReturn;

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
    void getLastModifiedByUserIdTest() {
        LocalDateTime localDateTime = LocalDateTime.parse("2024-01-01T10:15:30");
        doReturn(localDateTime).when(cartPersistencePortMock).findLastModifiedByUserId(5L);
        CartUseCase target = new CartUseCase(cartPersistencePortMock, authenticationPersistencePortMock, stockConnectionPersistencePortMock, supplyConnectionPersistencePortMock);

        LocalDateTime result = target.getLastModifiedByUserId(5L);

        assertAll("result", () -> {
            assertThat(result, equalTo(localDateTime));
            verify(cartPersistencePortMock).findLastModifiedByUserId(5L);
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
