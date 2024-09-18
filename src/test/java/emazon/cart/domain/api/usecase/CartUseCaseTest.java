package emazon.cart.domain.api.usecase;

import emazon.cart.domain.exception.CategoriesLimitException;
import emazon.cart.domain.exception.InsufficientStockException;
import emazon.cart.domain.exception.JsonParsingException;
import emazon.cart.domain.exception.NotFoundException;
import emazon.cart.domain.model.Cart;
import emazon.cart.domain.spi.IAuthenticationPersistencePort;
import emazon.cart.domain.spi.ICartPersistencePort;
import emazon.cart.domain.spi.IStockConnectionPersistencePort;
import emazon.cart.domain.spi.ISupplyConnectionPersistencePort;
import org.junit.jupiter.api.Test;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
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
    void addProductToCartWhenStockConnectionPersistencePortNotExistByIdProductIdThrowsNotFoundException() {
        doReturn(1L).when(authenticationPersistencePortMock).getAuthenticatedUserId();
        doReturn(false).when(stockConnectionPersistencePortMock).existById(1L);
        CartUseCase target = new CartUseCase(cartPersistencePortMock, authenticationPersistencePortMock, stockConnectionPersistencePortMock, supplyConnectionPersistencePortMock);
        Cart cart = new Cart();
        cart.setProductId(1L);
        cart.setUserId(1L);
        NotFoundException notFoundException = new NotFoundException("Product not found");

        final NotFoundException result = assertThrows(NotFoundException.class, () -> target.addProductToCart(cart));

        assertAll("result", () -> {
            assertThat(result, is(notNullValue()));
            assertThat(result.getMessage(), equalTo(notFoundException.getMessage()));
            verify(authenticationPersistencePortMock).getAuthenticatedUserId();
            verify(stockConnectionPersistencePortMock).existById(1L);
        });
    }

    @Test
    void addProductToCartWhenNextSupplyDateIsNullThrowsJsonParsingException() {
        doReturn(2L).when(authenticationPersistencePortMock).getAuthenticatedUserId();
        doReturn(true).when(stockConnectionPersistencePortMock).existById(2L);
        doReturn(false).when(stockConnectionPersistencePortMock).isStockSufficient(2L, 1);
        doReturn(null).when(supplyConnectionPersistencePortMock).getNextSupplyDate(2L);
        CartUseCase target = new CartUseCase(cartPersistencePortMock, authenticationPersistencePortMock, stockConnectionPersistencePortMock, supplyConnectionPersistencePortMock);
        Cart cart = new Cart();
        cart.setQuantity(1);
        cart.setProductId(2L);
        cart.setUserId(2L);
        JsonParsingException jsonParsingException = new JsonParsingException("Error parsing next supply date");

        final JsonParsingException result = assertThrows(JsonParsingException.class, () -> target.addProductToCart(cart));

        assertAll("result", () -> {
            assertThat(result, is(notNullValue()));
            assertThat(result.getMessage(), equalTo(jsonParsingException.getMessage()));
            verify(authenticationPersistencePortMock).getAuthenticatedUserId();
            verify(stockConnectionPersistencePortMock).existById(2L);
            verify(stockConnectionPersistencePortMock).isStockSufficient(2L, 1);
            verify(supplyConnectionPersistencePortMock).getNextSupplyDate(2L);
        });
    }

    @Test
    void addProductToCartWhenNextSupplyDateIsNotNullThrowsInsufficientStockException() {
        doReturn(3L).when(authenticationPersistencePortMock).getAuthenticatedUserId();
        doReturn(true).when(stockConnectionPersistencePortMock).existById(3L);
        doReturn(false).when(stockConnectionPersistencePortMock).isStockSufficient(3L, 1);
        doReturn("2024-01-01T10:15:30").when(supplyConnectionPersistencePortMock).getNextSupplyDate(3L);
        CartUseCase target = new CartUseCase(cartPersistencePortMock, authenticationPersistencePortMock, stockConnectionPersistencePortMock, supplyConnectionPersistencePortMock);
        Cart cart = new Cart();
        cart.setQuantity(1);
        cart.setProductId(3L);
        cart.setUserId(3L);
        InsufficientStockException insufficientStockException = new InsufficientStockException("Insufficient stock", "2024-01-01T10:15:30");

        final InsufficientStockException result = assertThrows(InsufficientStockException.class, () -> target.addProductToCart(cart));

        assertAll("result", () -> {
            assertThat(result, is(notNullValue()));
            assertThat(result.getMessage(), equalTo(insufficientStockException.getMessage()));
            verify(authenticationPersistencePortMock).getAuthenticatedUserId();
            verify(stockConnectionPersistencePortMock).existById(3L);
            verify(stockConnectionPersistencePortMock).isStockSufficient(3L, 1);
            verify(supplyConnectionPersistencePortMock).getNextSupplyDate(3L);
        });
    }

    @Test
    void addProductToCartWhenExistingCartIsNotNull() {
        doReturn(4L).when(authenticationPersistencePortMock).getAuthenticatedUserId();
        doReturn(true).when(stockConnectionPersistencePortMock).existById(4L);
        doReturn(true).when(stockConnectionPersistencePortMock).isStockSufficient(4L, 1);
        Cart cartMock = mock(Cart.class);
        doReturn(cartMock).when(cartPersistencePortMock).findProductByUserIdAndProductId(4L, 4L);
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
        cart.setProductId(4L);
        cart.setUserId(4L);
        cart.setUpdatedAt(date2);

        target.addProductToCart(cart);

        assertAll("result", () -> {
            verify(authenticationPersistencePortMock).getAuthenticatedUserId();
            verify(stockConnectionPersistencePortMock).existById(4L);
            verify(stockConnectionPersistencePortMock).isStockSufficient(4L, 1);
            verify(cartPersistencePortMock).findProductByUserIdAndProductId(4L, 4L);
            verify(cartMock).getQuantity();
            verify(cartMock).setQuantity(2);
            verify(cartMock).setUpdatedAt( any());
            verify(cartMock).getCreatedAt();
            verify(cartMock).getUpdatedAt();
            verify(cartPersistencePortMock).addProductToCart(cartMock);
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

    @Test
    void checkCategoriesLimitTest() throws Exception {
        List<Long> productIds = Arrays.asList(1L, 2L, 3L, 4L); // Add a fourth product ID to exceed the category limit
        List<String> categories1 = Arrays.asList("Electronics", "Books");
        List<String> categories2 = Arrays.asList("Electronics", "Toys");
        List<String> categories3 = List.of("Electronics");
        List<String> categories4 = List.of("Electronics"); // This should trigger the exception

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

    @Test
    void addProductToCartWhenExistingCartIsNull() {
        // Mock dependencies
        doReturn(5L).when(authenticationPersistencePortMock).getAuthenticatedUserId();
        doReturn(true).when(stockConnectionPersistencePortMock).existById(5L);
        doReturn(true).when(stockConnectionPersistencePortMock).isStockSufficient(5L, 1);
        doReturn(null).when(cartPersistencePortMock).findProductByUserIdAndProductId(5L, 5L);
        doReturn(Arrays.asList(1L, 2L, 3L)).when(cartPersistencePortMock).findProductIdsByUserId(5L);
        doNothing().when(cartPersistencePortMock).addProductToCart(any(Cart.class));

        // Create CartUseCase instance
        CartUseCase target = new CartUseCase(cartPersistencePortMock, authenticationPersistencePortMock, stockConnectionPersistencePortMock, supplyConnectionPersistencePortMock);

        // Create Cart object
        Cart cart = new Cart();
        cart.setQuantity(1);
        cart.setProductId(5L);
        cart.setUserId(5L);

        // Call the method
        target.addProductToCart(cart);

        // Verify interactions
        assertAll("result", () -> {
            verify(authenticationPersistencePortMock).getAuthenticatedUserId();
            verify(stockConnectionPersistencePortMock).existById(5L);
            verify(stockConnectionPersistencePortMock).isStockSufficient(5L, 1);
            verify(cartPersistencePortMock).findProductByUserIdAndProductId(5L, 5L);
            verify(cartPersistencePortMock).findProductIdsByUserId(5L);
            verify(cartPersistencePortMock).addProductToCart(any(Cart.class));
        });
    }
}
