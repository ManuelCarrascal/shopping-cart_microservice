package emazon.cart.infrastructure.config.feign;

import emazon.cart.domain.model.Pagination;
import emazon.cart.domain.model.Product;
import emazon.cart.domain.model.dto.ProductDetailsCart;
import emazon.cart.domain.model.dto.ProductListCartDomain;
import emazon.cart.infrastructure.config.utils.FeignConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = FeignConstants.STOCK_SERVICE_NAME, url = FeignConstants.STOCK_SERVICE_URL, configuration = FeignConfig.class)
public interface IStockFeignClient {

    @GetMapping("/products/{productId}")
    boolean existById(@PathVariable Long productId);

    @GetMapping("/products/stock/{productId}/{quantity}")
    boolean isStockSufficient(@PathVariable Long productId, @PathVariable Integer quantity);

    @GetMapping("/categories/{productId}/category-names")
    List<String> getCategoryNamesByProductId(@PathVariable Long productId);

    @GetMapping("/products/products-cart")
    Pagination<ProductDetailsCart> getProductsCart(
            @RequestParam(defaultValue = FeignConstants.DEFAULT_PAGE,  required = false) int page,
            @RequestParam(defaultValue = FeignConstants.DEFAULT_SIZE, required = false) int size,
            @RequestParam(defaultValue = FeignConstants.DEFAULT_IS_ASCENDING, required = false )boolean isAscending,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String brandName,
            @RequestBody ProductListCartDomain productListCartDomain
    );

    @GetMapping("/products/{productId}/price")
    Double getProductPriceById(@PathVariable Long productId);

    @GetMapping("/products/get-all")
    List<Product> getAllProducts();

}
