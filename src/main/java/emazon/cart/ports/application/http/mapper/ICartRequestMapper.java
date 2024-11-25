package emazon.cart.ports.application.http.mapper;

import emazon.cart.domain.model.Cart;
import emazon.cart.ports.application.http.dto.cart.CartRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ICartRequestMapper {
    Cart cartRequestToCart(CartRequest cartRequest);
}
