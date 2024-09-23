package emazon.cart.ports.application.http.controller;

import emazon.cart.domain.api.ICartServicePort;
import emazon.cart.domain.model.Cart;
import emazon.cart.domain.model.Pagination;
import emazon.cart.ports.application.http.dto.CartRequest;
import emazon.cart.ports.application.http.dto.CartResponse;
import emazon.cart.ports.application.http.dto.ProductResponse;
import emazon.cart.ports.application.http.mapper.ICartRequestMapper;
import emazon.cart.ports.application.http.mapper.ICartResponseMapper;
import emazon.cart.ports.application.http.util.RolePermissionConstants;
import emazon.cart.ports.application.http.util.openapi.CartRestControllerConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = CartRestControllerConstants.TAG_NAME, description = CartRestControllerConstants.TAG_DESCRIPTION)
public class CartRestController {

    private final ICartRequestMapper cartRequestMapper;
    private final ICartResponseMapper cartResponseMapper;
    private final ICartServicePort cartServicePort;

    @PreAuthorize(RolePermissionConstants.HAS_ROLE_CLIENTE)
    @PostMapping("/add")
    @Operation(summary = CartRestControllerConstants.ADD_PRODUCT_SUMMARY, description = CartRestControllerConstants.ADD_PRODUCT_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = CartRestControllerConstants.RESPONSE_CODE_201, description = CartRestControllerConstants.ADD_PRODUCT_RESPONSE_201_DESCRIPTION),
            @ApiResponse(responseCode = CartRestControllerConstants.RESPONSE_CODE_400, description = CartRestControllerConstants.ADD_PRODUCT_RESPONSE_400_DESCRIPTION)
    })
    public ResponseEntity<CartResponse> addProductToCart(@Parameter(description = CartRestControllerConstants.CART_REQUEST_DESCRIPTION, required = true) @Valid @RequestBody CartRequest cartRequest) {
        Cart cart = cartRequestMapper.cartRequestToCart(cartRequest);
        cartServicePort.addProductToCart(cart);
        CartResponse cartResponse = cartResponseMapper.cartToCartResponse(cart);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartResponse);
    }

    @PreAuthorize(RolePermissionConstants.HAS_ROLE_CLIENTE)
    @DeleteMapping("/delete/{userId}/{productId}")
    @Operation(summary = CartRestControllerConstants.REMOVE_PRODUCT_SUMMARY, description = CartRestControllerConstants.REMOVE_PRODUCT_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = CartRestControllerConstants.RESPONSE_CODE_200, description = CartRestControllerConstants.REMOVE_PRODUCT_RESPONSE_200_DESCRIPTION),
    })
    public ResponseEntity<String> removeProductFromCart(@PathVariable Long userId, @PathVariable Long productId) {
        cartServicePort.removeProductToCart(userId, productId);
        return ResponseEntity.status(HttpStatus.OK).body(CartRestControllerConstants.REMOVE_PRODUCT_RESPONSE_BODY);
    }

    @PreAuthorize(RolePermissionConstants.HAS_ROLE_CLIENTE)
    @GetMapping()
    public ResponseEntity<Pagination<ProductResponse>> getCartByUserId(
            @RequestParam(defaultValue = CartRestControllerConstants.DEFAULT_PAGE, required = false) int page,
            @RequestParam(defaultValue = CartRestControllerConstants.DEFAULT_SIZE, required = false) int size,
            @RequestParam(defaultValue = CartRestControllerConstants.DEFAULT_IS_ASCENDING, required = false) boolean isAscending,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String brandName) {
        Pagination<ProductResponse> productIds = cartServicePort.findProductIdsByUserId(page, size, isAscending, categoryName, brandName);
        return ResponseEntity.status(HttpStatus.OK).body(productIds);
    }

}
