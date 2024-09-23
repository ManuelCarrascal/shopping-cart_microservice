package emazon.cart.ports.application.http.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import emazon.cart.ports.application.http.dto.brand.BrandProductResponse;
import emazon.cart.ports.application.http.dto.category.CategoryProductResponse;
import emazon.cart.ports.application.http.util.openapi.product.ProductResponseConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = ProductResponseConstants.PRODUCT_RESPONSE_DESCRIPTION)
public class ProductResponse {
    @Schema(description = ProductResponseConstants.PRODUCT_ID_DESCRIPTION, example = ProductResponseConstants.PRODUCT_ID_EXAMPLE)
    private Long productId;
    @Schema(description = ProductResponseConstants.PRODUCT_NAME_DESCRIPTION, example = ProductResponseConstants.PRODUCT_NAME_EXAMPLE)
    private String productName;
    @Schema(description = ProductResponseConstants.PRODUCT_DESCRIPTION_DESCRIPTION, example = ProductResponseConstants.PRODUCT_DESCRIPTION_EXAMPLE)
    private String productDescription;
    @Schema(description = ProductResponseConstants.PRODUCT_QUANTITY_DESCRIPTION, example = ProductResponseConstants.PRODUCT_QUANTITY_EXAMPLE)
    private Integer productQuantity;
    @Schema(description = ProductResponseConstants.PRODUCT_PRICE_DESCRIPTION, example = ProductResponseConstants.PRODUCT_PRICE_EXAMPLE)
    private Double productPrice;
    @Schema(description = ProductResponseConstants.BRAND_DESCRIPTION)
    private BrandProductResponse brand;
    @Schema(description = ProductResponseConstants.CATEGORIES_DESCRIPTION)
    private List<CategoryProductResponse> categories;
    @Temporal(TemporalType.DATE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nextSupplyDate;
}
