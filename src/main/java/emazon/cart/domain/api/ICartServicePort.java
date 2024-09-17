package emazon.cart.domain.api;

import emazon.cart.domain.model.Cart;

import java.time.LocalDateTime;

public interface ICartServicePort {
    void addProductToCart(Cart cart);

    LocalDateTime getLastModifiedByUserId(Long userId);
}
