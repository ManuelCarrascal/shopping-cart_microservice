package emazon.cart.infrastructure.config.feign;

import emazon.cart.infrastructure.config.utils.FeignConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = FeignConstants.TRANSACTION_SERVICE_NAME, url = FeignConstants.TRANSACTION_SERVICE_URL, configuration = FeignConfig.class)
public interface ISupplyFeignClient {
    @GetMapping("/api/supply/get/next-supply-date/{productId}")
    String getNextSupplyDate(@PathVariable Long productId);
}
