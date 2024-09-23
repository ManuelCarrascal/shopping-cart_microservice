package emazon.cart.domain.api;

import emazon.cart.domain.model.Cart;
import emazon.cart.domain.model.Pagination;
import emazon.cart.ports.application.http.dto.ProductResponse;


public interface ICartServicePort {
    void addProductToCart(Cart cart);
    void removeProductToCart(Long userId, Long productId );

    Pagination<ProductResponse> findProductIdsByUserId(int page, int size, boolean isAscending, String categoryName, String brandName);

}
