package emazon.cart.domain.api;

import emazon.cart.domain.model.Cart;
import emazon.cart.domain.model.Pagination;
import emazon.cart.domain.model.dto.ProductDetailsCart;

import java.util.List;


public interface ICartServicePort {
    void addProductToCart(Cart cart);

    void removeProductToCart(Long userId, Long productId );

    Pagination<ProductDetailsCart> findProductIdsByUserId(int page, int size, boolean isAscending, String categoryName, String brandName);

    List<Cart> findCartByUserId();

    void deleteCart();
}
