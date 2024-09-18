package emazon.cart.domain.spi;


public interface ISupplyConnectionPersistencePort {
    String getNextSupplyDate(Long productId);
}
