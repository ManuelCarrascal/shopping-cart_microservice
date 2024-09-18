package emazon.cart.domain.spi;


import java.util.List;

public interface IStockConnectionPersistencePort {
    boolean existById(Long productId);

    boolean isStockSufficient(Long productId, Integer quantity);

    List<String> getCategoryNamesByProductId(Long productId);

}
