package emazon.cart.domain.api.usecase;

import emazon.cart.domain.api.ICartServicePort;
import emazon.cart.domain.exception.CategoriesLimitException;
import emazon.cart.domain.exception.InsufficientStockException;
import emazon.cart.domain.exception.JsonParsingException;
import emazon.cart.domain.exception.NotFoundException;
import emazon.cart.domain.model.Cart;
import emazon.cart.domain.spi.*;
import emazon.cart.domain.util.CartUseCaseConstants;


import java.time.LocalDateTime;
import java.util.*;

public class CartUseCase implements ICartServicePort {
    private final ICartPersistencePort cartPersistencePort;
    private final IAuthenticationPersistencePort authenticationPersistencePort;
    private final IStockConnectionPersistencePort stockConnectionPersistencePort;
    private final ISupplyConnectionPersistencePort supplyConnectionPersistencePort;

    public CartUseCase(
            ICartPersistencePort cartPersistencePort,
            IAuthenticationPersistencePort authenticationPersistencePort,
            IStockConnectionPersistencePort stockConnectionPersistencePort,
            ISupplyConnectionPersistencePort supplyConnectionPersistencePort
    ) {
        this.cartPersistencePort = cartPersistencePort;
        this.authenticationPersistencePort = authenticationPersistencePort;
        this.stockConnectionPersistencePort = stockConnectionPersistencePort;
        this.supplyConnectionPersistencePort = supplyConnectionPersistencePort;
    }

    @Override
    public void addProductToCart(Cart cart) {
        Long userId = authenticationPersistencePort.getAuthenticatedUserId();
        cart.setUserId(userId);

        validateProductExistence(cart.getProductId());

        validateStockAvailability(cart);

        Cart existingCart = cartPersistencePort.findProductByUserIdAndProductId(userId, cart.getProductId());

        if (existingCart != null) {
            updateExistingCart(existingCart, cart.getQuantity());
            cart.setCreatedAt(existingCart.getCreatedAt());
            cart.setUpdatedAt(existingCart.getUpdatedAt());
        } else {
            List<Long> productsCart = new ArrayList<>(cartPersistencePort.findProductIdsByUserId(userId));
            productsCart.add(cart.getProductId());
            checkCategoriesLimit(productsCart);

            createNewCart(cart);
        }
    }

    private void validateProductExistence(Long productId) {
        if (!stockConnectionPersistencePort.existById(productId)) {
            throw new NotFoundException(CartUseCaseConstants.PRODUCT_NOT_FOUND);
        }
    }

    private void validateStockAvailability(Cart cart) {
        if (!stockConnectionPersistencePort.isStockSufficient(cart.getProductId(), cart.getQuantity())) {
            String nextSupplyDate = supplyConnectionPersistencePort.getNextSupplyDate(cart.getProductId());
            if (nextSupplyDate == null) {
                throw new JsonParsingException(CartUseCaseConstants.ERROR_PARSING_NEXT_SUPPLY_DATE);
            }
            throw new InsufficientStockException(CartUseCaseConstants.INSUFFICIENT_STOCK, nextSupplyDate);
        }
    }

    private void updateExistingCart(Cart existingCart, int quantityToAdd){
        existingCart.setQuantity(existingCart.getQuantity() + quantityToAdd);
        existingCart.setUpdatedAt(new Date());
        cartPersistencePort.addProductToCart(existingCart);
    }

    private void createNewCart(Cart cart){
        cart.setCreatedAt(new Date());
        cart.setUpdatedAt(new Date());
        cartPersistencePort.addProductToCart(cart);
    }

    public LocalDateTime getLastModifiedByUserId(Long userId) {
        return cartPersistencePort.findLastModifiedByUserId(userId);
    }

    private void checkCategoriesLimit(List<Long> productIds) {
        Map<String, Integer> categoryCountMap = new HashMap<>();
        for (Long productId : productIds) {
            List<String> categories = stockConnectionPersistencePort.getCategoryNamesByProductId(productId);
            for (String category : categories) {
                categoryCountMap.put(category, categoryCountMap.getOrDefault(category, CartUseCaseConstants.DEFAULT_CATEGORY_COUNT) + CartUseCaseConstants.INCREMENT_CATEGORY_COUNT);
                if (categoryCountMap.get(category) > CartUseCaseConstants.MAX_CATEGORY_LIMIT) {
                    throw new CategoriesLimitException(CartUseCaseConstants.CATEGORIES_LIMIT_EXCEEDED + category);
                }
            }
        }
    }
    }
