package emazon.cart.ports.application.http.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartRestController {

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/add/{productId}")
    public ResponseEntity<String> addProductToCart(@PathVariable Long productId) {
        return ResponseEntity.ok("Product added to cart");

    }


}
