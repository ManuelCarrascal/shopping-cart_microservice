package emazon.cart.ports.persistence.mysql.repository;

import emazon.cart.ports.persistence.mysql.entity.CartEntity;
import emazon.cart.ports.persistence.mysql.util.CartRepositoryConstants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ICartRepository extends JpaRepository<CartEntity, Long> {
    CartEntity findByUserIdAndProductId(Long userId, Long productId);

    @Query(CartRepositoryConstants.FIND_LAST_MODIFIED_BY_USER_ID_QUERY)
    LocalDateTime findLastModifiedByUserId(@Param(CartRepositoryConstants.USER_ID_PARAM) Long userId);


    List<CartEntity> findByUserId(Long userId);
}
