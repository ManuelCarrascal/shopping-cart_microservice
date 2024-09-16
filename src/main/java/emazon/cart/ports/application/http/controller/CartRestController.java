package emazon.cart.ports.application.http.controller;

import emazon.cart.domain.api.ICartServicePort;
import emazon.cart.domain.model.Cart;
import emazon.cart.ports.application.http.dto.CartRequest;
import emazon.cart.ports.application.http.dto.CartResponse;
import emazon.cart.ports.application.http.mapper.ICartRequestMapper;
import emazon.cart.ports.application.http.mapper.ICartResponseMapper;
import emazon.cart.ports.application.http.util.ResponseMessageConstants;
import emazon.cart.ports.application.http.util.RolePermissionConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartRestController {

    private final ICartRequestMapper cartRequestMapper;
    private final ICartResponseMapper cartResponseMapper;
    private final ICartServicePort cartServicePort;

    @PreAuthorize(RolePermissionConstants.HAS_ROLE_CLIENTE)
    @PostMapping("/add")
    public ResponseEntity<CartResponse> addProductToCart(@Valid @RequestBody CartRequest cartRequest) {
        Cart cart =cartRequestMapper.cartRequestToCart(cartRequest);
        cartServicePort.addProductToCart(cart);
        CartResponse cartResponse = cartResponseMapper.cartToCartResponse(cart);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartResponse);
    }

    @PreAuthorize(RolePermissionConstants.HAS_ROLE_CLIENTE)
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> removeProductToCart(@PathVariable Long productId) {
        Object principalRemoval = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        long userId;
        if (principalRemoval instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            userId = Long.parseLong(username);
        } else {
            userId = Long.parseLong(principalRemoval.toString());
        }

        return ResponseEntity.ok(String.format(ResponseMessageConstants.PRODUCT_REMOVED_FROM_CART, productId, userId));
    }


}
