package emazon.cart.domain.spi;

public interface IStockConnectionPersistencePort {
    boolean existById(Long productId);

    boolean isStockSufficient(Long productId, Integer quantity);
}
