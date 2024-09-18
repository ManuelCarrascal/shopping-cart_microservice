package emazon.cart.ports.persistence.mysql.adapter;

import emazon.cart.domain.spi.IStockConnectionPersistencePort;
import emazon.cart.infrastructure.config.feign.IStockFeignClient;
import feign.FeignException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
public class StockConnectionAdapter implements IStockConnectionPersistencePort {

    private final IStockFeignClient stockFeignClient;

    public boolean existById(Long id) {
        try{
            return stockFeignClient.existById(id);
        }catch (FeignException.NotFound e){
            return false;
        }
    }

    public boolean isStockSufficient(Long productId, Integer quantity) {
        try{
            return stockFeignClient.isStockSufficient(productId, quantity);
        }catch (FeignException.NotFound e){
            return false;
        }
    }

    public List<String> getCategoryNamesByProductId(Long productId) {
        try{
            return stockFeignClient.getCategoryNamesByProductId(productId);
        }catch (FeignException.NotFound e){
            return Collections.emptyList();
        }
    }


}
