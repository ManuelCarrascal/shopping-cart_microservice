package emazon.cart.ports.application.http.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class CartResponse {
    private Integer quantity;
    private Long productId;
    private Long userId;
    private Date createdAt;
    private Date updatedAt;

}
