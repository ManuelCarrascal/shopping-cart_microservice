package emazon.cart.ports.persistence.mysql.adapter;

import emazon.cart.domain.model.Cart;
import emazon.cart.domain.spi.ICartPersistencePort;
import emazon.cart.ports.persistence.mysql.mapper.ICartEntityMapper;
import emazon.cart.ports.persistence.mysql.repository.ICartRepository;
import lombok.RequiredArgsConstructor;

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
}
