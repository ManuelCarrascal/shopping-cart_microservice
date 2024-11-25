package emazon.cart.ports.application.http.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CartUpdateQuantityRequest {
    private Long productId;
    private int quantity;
}
