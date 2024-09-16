package emazon.cart.domain.spi;

import emazon.cart.domain.model.Cart;

public interface ICartPersistencePort {
    void addProductToCart(Cart cart);

    Cart findProductByUserIdAndProductId(Long userId, Long productId);
}
