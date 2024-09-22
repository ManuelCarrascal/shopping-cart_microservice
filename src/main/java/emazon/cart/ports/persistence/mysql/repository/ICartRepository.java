package emazon.cart.ports.persistence.mysql.repository;

import emazon.cart.ports.persistence.mysql.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ICartRepository extends JpaRepository<CartEntity, Long> {

    CartEntity findByUserIdAndProductId(Long userId, Long productId);

    List<CartEntity> findByUserId(Long userId);

    @Query("SELECT c FROM CartEntity c WHERE c.userId = :userId")
    List<CartEntity> findAllByUserId(@Param("userId") Long userId);
}
