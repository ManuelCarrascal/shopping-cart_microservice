package emazon.cart.domain.api;

import emazon.cart.domain.model.Cart;

import java.time.LocalDateTime;

public interface ICartServicePort {
    void addProductToCart(Cart cart);
    void removeProductToCart(Long userId, Long productId );
    LocalDateTime getLastModifiedByUserId(Long userId);
}
