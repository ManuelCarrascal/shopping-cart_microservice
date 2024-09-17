package emazon.cart.ports.persistence.mysql.repository;

import emazon.cart.ports.persistence.mysql.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ICartRepository extends JpaRepository<CartEntity, Long> {
    CartEntity findByUserIdAndProductId(Long userId, Long productId);

    @Query("SELECT MAX(c.updatedAt) FROM CartEntity c WHERE c.userId = :userId")
    LocalDateTime findLastModifiedByUserId(@Param("userId") Long userId);
}
