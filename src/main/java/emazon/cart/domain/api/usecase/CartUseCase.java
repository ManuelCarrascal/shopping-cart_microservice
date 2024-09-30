package emazon.cart.domain.api.usecase;

import emazon.cart.domain.api.ICartServicePort;
import emazon.cart.domain.exception.CategoriesLimitException;
import emazon.cart.domain.exception.InsufficientStockException;
import emazon.cart.domain.exception.NotFoundException;
import emazon.cart.domain.model.Cart;
import emazon.cart.domain.model.Pagination;
import emazon.cart.domain.model.dto.ProductDetailsCart;
import emazon.cart.domain.model.dto.ProductListCartDomain;
import emazon.cart.domain.spi.*;
import emazon.cart.domain.util.CartUseCaseConstants;


import java.util.*;

public  class   CartUseCase implements ICartServicePort {
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

        Cart existingCart = cartPersistencePort.findProductByUserIdAndProductId(userId, cart.getProductId());

        int totalQuantity = cart.getQuantity();

        if (existingCart != null) {
            totalQuantity += existingCart.getQuantity();
        }

        validateStockAvailability(cart.getProductId(), totalQuantity);

        if (existingCart != null) {
            updateExistingCart(existingCart, cart.getQuantity());
            cart.setCreatedAt(existingCart.getCreatedAt());
            cart.setUpdatedAt(existingCart.getUpdatedAt());
            return;
        }

        List<Long> productsCart = new ArrayList<>(cartPersistencePort.findProductIdsByUserId(userId));
        productsCart.add(cart.getProductId());
        checkCategoriesLimit(productsCart);
        createNewCart(cart);
    }

    @Override
    public void removeProductToCart(Long userId, Long productId) {
        Cart existingCart = cartPersistencePort.findProductByUserIdAndProductId(userId, productId);
        if (existingCart == null) {
            throw new NotFoundException(CartUseCaseConstants.PRODUCT_NOT_FOUND);
        }
        cartPersistencePort.removeProductFromCart(userId, productId);
        cartPersistencePort.updateCartItemsUpdatedAt(userId, new Date());

    }

    @Override
    public Pagination<ProductDetailsCart> findProductIdsByUserId(int page, int size, boolean isAscending, String categoryName, String brandName) {
        Long userId = authenticationPersistencePort.getAuthenticatedUserId();
        ProductListCartDomain productListCartDomain = createProductCartRequest(userId);
        Pagination<ProductDetailsCart> productResponsePagination = stockConnectionPersistencePort.getAllProductsPaginatedByIds(page, size, isAscending, categoryName, brandName, productListCartDomain);

        double total = calculateTotalForAllItems(userId);
        updateProductDetailsInCart(userId, productResponsePagination);
        productResponsePagination.setTotal(total);
        return productResponsePagination;
    }

    @Override
    public List<Cart> findCartByUserId() {
        Long userId = authenticationPersistencePort.getAuthenticatedUserId();
        return cartPersistencePort.findCartByUserId(userId);
    }

    @Override
    public void deleteCart() {
        Long userId = authenticationPersistencePort.getAuthenticatedUserId();
        cartPersistencePort.deleteCart(userId);
    }


    private ProductListCartDomain createProductCartRequest(Long userId) {
        ProductListCartDomain productListCartDomain = new ProductListCartDomain();
        productListCartDomain.setProductIds(cartPersistencePort.findProductIdsByUserId(userId));
        return productListCartDomain;
    }

    private double calculateTotalForAllItems(Long userId) {
        List<Long> productIds = cartPersistencePort.findProductIdsByUserId(userId);
        double total = CartUseCaseConstants.DEFAULT_TOTAL;
        for (Long productId : productIds) {
            Cart cart = cartPersistencePort.findProductByUserIdAndProductId(userId, productId);
            if (cart != null) {
                total += cart.getQuantity() * stockConnectionPersistencePort.getProductPriceById(productId);
            }
        }
        return total;
    }

    private void updateProductDetailsInCart(Long userId, Pagination<ProductDetailsCart> productResponsePagination) {
        for (ProductDetailsCart productResponse : productResponsePagination.getContent()) {
            Cart cart = cartPersistencePort.findProductByUserIdAndProductId(userId, productResponse.getProductId());
            if (cart == null) {
                continue;
            }

            int cartQuantity = cart.getQuantity();
            double subtotal = productResponse.getProductPrice() * cartQuantity;
            productResponse.setSubtotal(subtotal);
            if (productResponse.getProductQuantity() == CartUseCaseConstants.DEFAULT_PRODUCT_QUANTITY || productResponse.getProductQuantity() < cartQuantity) {
                String nextSupplyDate = supplyConnectionPersistencePort.getNextSupplyDate(productResponse.getProductId());
                productResponse.setNextSupplyDate(nextSupplyDate);
            }
            productResponse.setCartQuantity(cartQuantity);
        }
    }

    private void validateProductExistence(Long productId) {
        if (!stockConnectionPersistencePort.existById(productId)) {
            throw new NotFoundException(CartUseCaseConstants.PRODUCT_NOT_FOUND);
        }
    }

    private void validateStockAvailability(Long productId, int totalQuantity) {
        if (!stockConnectionPersistencePort.isStockSufficient(productId, totalQuantity)) {
            String nextSupplyDate = supplyConnectionPersistencePort.getNextSupplyDate(productId);
            throw new InsufficientStockException(CartUseCaseConstants.INSUFFICIENT_STOCK, nextSupplyDate);
        }
    }

    private void updateExistingCart(Cart existingCart, int quantityToAdd) {
        existingCart.setQuantity(existingCart.getQuantity() + quantityToAdd);
        existingCart.setUpdatedAt(new Date());
        cartPersistencePort.addProductToCart(existingCart);
    }

    private void createNewCart(Cart cart) {
        cart.setCreatedAt(new Date());
        cart.setUpdatedAt(new Date());
        cartPersistencePort.addProductToCart(cart);
    }

    private void checkCategoriesLimit(List<Long> productIds) {
        Map<String, Integer> categoryCountMap = new HashMap<>();
        for (Long productId : productIds) {
            List<String> categories = stockConnectionPersistencePort.getCategoryNamesByProductId(productId);
            updateCategoryCountMap(categoryCountMap, categories);
        }
    }

    private void updateCategoryCountMap(Map<String, Integer> categoryCountMap, List<String> categories) {
        for (String category : categories) {
            categoryCountMap.put(category, categoryCountMap.getOrDefault(category, CartUseCaseConstants.DEFAULT_CATEGORY_COUNT) + CartUseCaseConstants.INCREMENT_CATEGORY_COUNT);
            if (categoryCountMap.get(category) > CartUseCaseConstants.MAX_CATEGORY_LIMIT) {
                throw new CategoriesLimitException(CartUseCaseConstants.CATEGORIES_LIMIT_EXCEEDED + category);
            }
        }
    }

}
