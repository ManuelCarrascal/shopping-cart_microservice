package emazon.cart.domain.api.usecase;

import emazon.cart.domain.api.ICartServicePort;
import emazon.cart.domain.exception.InsufficientStockException;
import emazon.cart.domain.exception.NotFoundException;
import emazon.cart.domain.model.Cart;
import emazon.cart.domain.spi.IAuthenticationPersistencePort;
import emazon.cart.domain.spi.ICartPersistencePort;
import emazon.cart.domain.spi.IStockConnectionPersistencePort;


import java.time.LocalDateTime;
import java.util.Date;

public class CartUseCase implements ICartServicePort {
    private final ICartPersistencePort cartPersistencePort;
    private final IAuthenticationPersistencePort authenticationPersistencePort;
    private final IStockConnectionPersistencePort stockConnectionPersistencePort;

    public CartUseCase(ICartPersistencePort cartPersistencePort, IAuthenticationPersistencePort authenticationPersistencePort, IStockConnectionPersistencePort stockConnectionPersistencePort) {
        this.cartPersistencePort = cartPersistencePort;
        this.authenticationPersistencePort = authenticationPersistencePort;
        this.stockConnectionPersistencePort = stockConnectionPersistencePort;
    }

    @Override
    public void addProductToCart(Cart cart) {
        Long userId = authenticationPersistencePort.getAuthenticatedUserId();
        cart.setUserId(userId);
        if(!stockConnectionPersistencePort.existById(cart.getProductId())){
            throw new NotFoundException("Product not found");
        }
        if(!stockConnectionPersistencePort.isStockSufficient(cart.getProductId(), cart.getQuantity())){
            throw new InsufficientStockException("Insufficient stock");
        }

        Cart existingCart = cartPersistencePort.findProductByUserIdAndProductId(userId, cart.getProductId());
        if (existingCart != null) {
            updateExistingCart(existingCart, cart.getQuantity());
            cart.setCreatedAt(existingCart.getCreatedAt());
            cart.setUpdatedAt(existingCart.getUpdatedAt());
        } else {
            createNewCart(cart);
        }

    }

    private void updateExistingCart(Cart existingCart, int quantityToAdd){
        existingCart.setQuantity(existingCart.getQuantity() + quantityToAdd);
        existingCart.setUpdatedAt(new Date());
        cartPersistencePort.addProductToCart(existingCart);
    }

    private void createNewCart(Cart cart){
        cart.setCreatedAt(new Date());
        cart.setUpdatedAt(new Date());
        cartPersistencePort.addProductToCart(cart);
    }

    public LocalDateTime getLastModifiedByUserId(Long userId) {
        return cartPersistencePort.findLastModifiedByUserId(userId);
    }

}
