package emazon.cart.ports.persistence.mysql.mapper;

import emazon.cart.domain.model.Cart;
import emazon.cart.ports.persistence.mysql.entity.CartEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ICartEntityMapper {
    CartEntity toEntity(Cart cart);

    Cart toModel( CartEntity cartEntity);
}
