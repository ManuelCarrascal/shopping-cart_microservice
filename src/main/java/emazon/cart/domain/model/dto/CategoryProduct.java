package emazon.cart.domain.model.dto;

import emazon.cart.ports.application.http.util.openapi.category.CategoryProductResponseConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = CategoryProductResponseConstants.CATEGORY_PRODUCT_RESPONSE_DESCRIPTION)
public class CategoryProduct implements Serializable {
    @Schema(description = CategoryProductResponseConstants.CATEGORY_ID_DESCRIPTION, example = CategoryProductResponseConstants.CATEGORY_ID_EXAMPLE)
    private Long categoryId;

    @Schema(description = CategoryProductResponseConstants.CATEGORY_NAME_DESCRIPTION, example = CategoryProductResponseConstants.CATEGORY_NAME_EXAMPLE)
    private String categoryName;
}
