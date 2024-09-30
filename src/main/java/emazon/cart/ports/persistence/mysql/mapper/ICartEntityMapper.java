package emazon.cart.ports.persistence.mysql.mapper;

import emazon.cart.domain.model.Cart;
import emazon.cart.ports.persistence.mysql.entity.CartEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ICartEntityMapper {
    CartEntity toEntity(Cart cart);

    Cart toModel( CartEntity cartEntity);

    List<Cart> toModelList(List<CartEntity> byUserId);
}
