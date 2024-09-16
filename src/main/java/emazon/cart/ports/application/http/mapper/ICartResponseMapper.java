package emazon.cart.ports.application.http.mapper;

import emazon.cart.domain.model.Cart;
import emazon.cart.ports.application.http.dto.CartResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ICartResponseMapper {
    CartResponse cartToCartResponse(Cart cart);
}
