package emazon.cart.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductDetailsCart {
    private Long productId;
    private String productName;
    private String productDescription;
    private Integer productQuantity;
    private Double productPrice;
    private Integer cartQuantity;
    private BrandProduct brand;
    private List<CategoryProduct> categories;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nextSupplyDate;
    private Double subtotal;
}
