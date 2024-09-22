package emazon.cart.infrastructure.config.feign;

import emazon.cart.domain.model.Pagination;
import emazon.cart.infrastructure.config.utils.FeignConstants;
import emazon.cart.ports.application.http.dto.ProductResponse;
import emazon.cart.ports.application.http.dto.product.ProductCartRequest;
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
    Pagination<ProductResponse> getProductsCart(
            @RequestParam(defaultValue = "0",  required = false) int page,
            @RequestParam(defaultValue = "1", required = false) int size,
            @RequestParam(defaultValue = "true", required = false )boolean isAscending,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String brandName,
            @RequestBody ProductCartRequest productCartRequest
    );


}
