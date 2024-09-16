package emazon.cart.infrastructure.config.feign;

import emazon.cart.infrastructure.config.utils.FeignConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = FeignConstants.STOCK_SERVICE_NAME, url = FeignConstants.STOCK_SERVICE_URL, configuration = FeignConfig.class)
public interface IStockFeignClient {

    @GetMapping("/products/{productId}")
    boolean existById(@PathVariable Long productId);

    @GetMapping("/products/stock/{productId}/{quantity}")
    boolean isStockSufficient(@PathVariable Long productId, @PathVariable Integer quantity);
}
