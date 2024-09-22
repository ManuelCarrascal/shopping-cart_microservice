package emazon.cart.domain.api;

import emazon.cart.domain.model.Cart;

import java.util.List;

public interface ICartServicePort {
    void addProductToCart(Cart cart);
    void removeProductToCart(Long userId, Long productId );

    List<Long> findProductIdsByUserId( int page, int size, boolean isAscending, String categoryName, String brandName);

}
