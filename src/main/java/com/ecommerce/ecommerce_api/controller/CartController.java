package com.ecommerce.ecommerce_api.controller;

import com.ecommerce.ecommerce_api.dto.CartItemRequest;
import com.ecommerce.ecommerce_api.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartItemRequest request, Authentication authentication) {
        try {
            return ResponseEntity.ok(cartService.addToCart(request, authentication.getName()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> viewCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.viewCart(authentication.getName()));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long productId, Authentication authentication) {
        return ResponseEntity.ok(cartService.removeFromCart(productId, authentication.getName()));
    }
}