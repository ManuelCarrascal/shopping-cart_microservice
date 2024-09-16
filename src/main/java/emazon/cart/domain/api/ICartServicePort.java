package emazon.cart.domain.api;

import emazon.cart.domain.model.Cart;

public interface ICartServicePort {
    void addProductToCart(Cart cart);
}
