package emazon.cart.ports.persistence.mysql.adapter;

import emazon.cart.domain.spi.IStockConnectionPersistencePort;
import emazon.cart.infrastructure.config.feign.IStockFeignClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;

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


}
