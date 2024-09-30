package emazon.cart.ports.persistence.mysql.repository;

import emazon.cart.ports.persistence.mysql.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface ICartRepository extends JpaRepository<CartEntity, Long> {

    CartEntity findByUserIdAndProductId(Long userId, Long productId);

    List<CartEntity> findByUserId(Long userId);

    List<CartEntity> findCartByUserId(Long userId);

    void deleteByUserId(Long userId);
}
