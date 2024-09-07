package emazon.cart.domain.api.usecase;

import emazon.cart.domain.api.ICartServicePort;
import emazon.cart.domain.spi.ICartPersistencePort;

public class CartUseCase implements ICartServicePort {
    private final ICartPersistencePort cartPersistencePort;

    public CartUseCase(ICartPersistencePort cartPersistencePort) {
        this.cartPersistencePort = cartPersistencePort;
    }

}
