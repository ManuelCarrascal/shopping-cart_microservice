package emazon.cart.domain.spi;

import emazon.cart.domain.model.Cart;

import java.time.LocalDateTime;

public interface ICartPersistencePort {
    void addProductToCart(Cart cart);

    Cart findProductByUserIdAndProductId(Long userId, Long productId);

    LocalDateTime findLastModifiedByUserId(Long userId);
}
