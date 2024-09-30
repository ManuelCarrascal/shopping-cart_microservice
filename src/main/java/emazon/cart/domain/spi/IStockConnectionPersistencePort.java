package emazon.cart.domain.spi;



import emazon.cart.domain.model.Pagination;
import emazon.cart.domain.model.Product;
import emazon.cart.domain.model.dto.ProductDetailsCart;
import emazon.cart.domain.model.dto.ProductListCartDomain;

import java.util.List;

public interface IStockConnectionPersistencePort {
    boolean existById(Long productId);

    boolean isStockSufficient(Long productId, Integer quantity);

    List<String> getCategoryNamesByProductId(Long productId);

    Pagination<ProductDetailsCart> getAllProductsPaginatedByIds(
            int page,
            int size,
            boolean isAscending,
            String categoryName,
            String brandName,
            ProductListCartDomain productListCartDomain
            );

    Double getProductPriceById(Long productId);

    List<Product> getAllProducts(List<Long> productIds);
}
