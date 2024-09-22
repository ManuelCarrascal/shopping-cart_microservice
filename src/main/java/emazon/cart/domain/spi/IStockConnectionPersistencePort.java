package emazon.cart.domain.spi;



import emazon.cart.domain.model.Pagination;
import emazon.cart.ports.application.http.dto.ProductResponse;
import emazon.cart.ports.application.http.dto.product.ProductCartRequest;

import java.util.List;

public interface IStockConnectionPersistencePort {
    boolean existById(Long productId);

    boolean isStockSufficient(Long productId, Integer quantity);

    List<String> getCategoryNamesByProductId(Long productId);

    Pagination<ProductResponse> getAllProductsPaginatedByIds(
            int page,
            int size,
            boolean isAscending,
            String categoryName,
            String brandName,
            ProductCartRequest productCartRequest
            );


}
