package emazon.cart.infrastructure.config.beanconfiguration;

import emazon.cart.domain.api.ICartServicePort;
import emazon.cart.domain.api.usecase.CartUseCase;
import emazon.cart.domain.spi.IAuthenticationPersistencePort;
import emazon.cart.domain.spi.ICartPersistencePort;
import emazon.cart.domain.spi.IStockConnectionPersistencePort;
import emazon.cart.infrastructure.config.feign.IStockFeignClient;
import emazon.cart.ports.persistence.mysql.adapter.AuthenticationAdapter;
import emazon.cart.ports.persistence.mysql.adapter.CartAdapter;
import emazon.cart.ports.persistence.mysql.adapter.StockConnectionAdapter;
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
    public ICartServicePort cartServicePort(
            ICartPersistencePort cartPersistencePort,
            IAuthenticationPersistencePort authenticationPersistencePort,
            IStockConnectionPersistencePort stockConnectionPersistencePort
    ) {
        return new CartUseCase(cartPersistencePort, authenticationPersistencePort, stockConnectionPersistencePort);
    }

    @Bean
    public IAuthenticationPersistencePort authenticationPersistencePort() {
        return new AuthenticationAdapter();
    }



}