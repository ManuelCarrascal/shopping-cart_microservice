package emazon.cart.infrastructure.config.beanconfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import emazon.cart.domain.api.ICartServicePort;
import emazon.cart.domain.api.usecase.CartUseCase;
import emazon.cart.domain.spi.*;
import emazon.cart.infrastructure.config.feign.IStockFeignClient;
import emazon.cart.infrastructure.config.feign.ISupplyFeignClient;
import emazon.cart.ports.persistence.mysql.adapter.*;
import emazon.cart.ports.persistence.mysql.mapper.ICartEntityMapper;
import emazon.cart.ports.persistence.mysql.repository.ICartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    @Bean
    public ICartPersistencePort cartPersistencePort(ICartRepository cartRepository, ICartEntityMapper cartEntityMapper) {
        return new CartAdapter(cartRepository, cartEntityMapper);
    }
    @Bean
    public IStockConnectionPersistencePort stockConnectionPersistencePort(IStockFeignClient stockFeignClient) {
        return new StockConnectionAdapter(stockFeignClient);
    }
    @Bean
    public ISupplyConnectionPersistencePort supplyConnectionPersistencePort(ISupplyFeignClient  supplyFeignClient, ObjectMapper objectMapper) {
        return new SupplyConnectionAdapter(supplyFeignClient, objectMapper);
    }

    @Bean
    public ICartServicePort cartServicePort(
            ICartPersistencePort cartPersistencePort,
            IAuthenticationPersistencePort authenticationPersistencePort,
            IStockConnectionPersistencePort stockConnectionPersistencePort,
            ISupplyConnectionPersistencePort supplyConnectionPersistencePort
    ) {
        return new CartUseCase(cartPersistencePort, authenticationPersistencePort, stockConnectionPersistencePort,  supplyConnectionPersistencePort);
    }


    @Bean
    public IAuthenticationPersistencePort authenticationPersistencePort() {
        return new AuthenticationAdapter();
    }
}