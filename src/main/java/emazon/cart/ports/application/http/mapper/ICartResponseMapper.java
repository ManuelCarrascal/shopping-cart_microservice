package emazon.cart.ports.application.http.mapper;

import emazon.cart.domain.model.Cart;
import emazon.cart.domain.model.dto.ProductDetailsCart;
import emazon.cart.ports.application.http.dto.cart.CartResponse;
import emazon.cart.ports.application.http.dto.product.ProductResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ICartResponseMapper {
    CartResponse cartToCartResponse(Cart cart);
    ProductResponse productDetailsCartToProductResponse(ProductDetailsCart productDetailsCart);

    List<ProductResponse> productDetailsCartListToProductResponseList(List<ProductDetailsCart> productDetailsCarts);

    List<CartResponse> cartListToCartResponseList(List<Cart> carts);
}
