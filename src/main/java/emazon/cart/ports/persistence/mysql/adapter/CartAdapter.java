package emazon.cart.ports.persistence.mysql.adapter;

import emazon.cart.domain.model.Cart;
import emazon.cart.domain.spi.ICartPersistencePort;
import emazon.cart.ports.persistence.mysql.entity.CartEntity;
import emazon.cart.ports.persistence.mysql.mapper.ICartEntityMapper;
import emazon.cart.ports.persistence.mysql.repository.ICartRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
public class CartAdapter implements ICartPersistencePort {

    private final ICartRepository cartRepository;
    private final ICartEntityMapper cartEntityMapper;

    @Override
    public void addProductToCart(Cart cart) {
        cartRepository.save(cartEntityMapper.toEntity(cart));
    }

    @Override
    public Cart findProductByUserIdAndProductId(Long userId, Long productId) {
        return cartEntityMapper.toModel(cartRepository.findByUserIdAndProductId(userId, productId));
    }

    @Override
    public LocalDateTime findLastModifiedByUserId(Long userId) {
        return cartRepository.findLastModifiedByUserId(userId);
    }

    @Override
    public List<Long> findProductIdsByUserId(Long userId) {
        return cartRepository.findByUserId(userId).stream()
                .map(CartEntity::getProductId)
                .toList();
    }

    @Override
    public void removeProductFromCart(Long userId, Long productId) {
        CartEntity cartEntity = cartRepository.findByUserIdAndProductId(userId, productId);
        cartRepository.delete(cartEntity);
    }

    @Override
    public void updateCartItemsUpdatedAt(Long userId, Date updatedAt) {
        List<CartEntity> cartEntities = cartRepository.findByUserId(userId);
        for (CartEntity cartEntity : cartEntities) {
            cartEntity.setUpdatedAt(updatedAt);
            cartRepository.save(cartEntity);
        }
    }

}
