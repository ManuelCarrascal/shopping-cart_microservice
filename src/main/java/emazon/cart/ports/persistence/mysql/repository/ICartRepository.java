package emazon.cart.ports.persistence.mysql.repository;

import emazon.cart.ports.persistence.mysql.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICartRepository extends JpaRepository<CartEntity, Long> {
    CartEntity findByUserIdAndProductId(Long userId, Long productId);
}
