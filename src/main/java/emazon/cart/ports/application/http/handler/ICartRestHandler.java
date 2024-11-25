package emazon.cart.ports.application.http.handler;

import emazon.cart.domain.model.Pagination;
import emazon.cart.ports.application.http.dto.product.ProductResponse;

public interface ICartRestHandler {

    Pagination<ProductResponse> findProductIdsByUserId(int page, int size, boolean isAscending, String categoryName, String brandName);

}
