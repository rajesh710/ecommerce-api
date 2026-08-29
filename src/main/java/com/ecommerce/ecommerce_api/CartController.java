package com.ecommerce.ecommerce_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private Cart getOrCreateCart(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);

        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setItems(new ArrayList<>());
            cartRepository.save(cart);
        }
        return cart;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartItemRequest request, Authentication authentication) {
        Cart cart = getOrCreateCart(authentication.getName());

        Product product = productRepository.findById(request.getProductId()).orElse(null);
        if (product == null) {
            return ResponseEntity.badRequest().body("Product not found");
        }

        // check if this product is already in the cart, update quantity instead of duplicating
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + request.getQuantity());
                cartRepository.save(cart);
                return ResponseEntity.ok(cart);
            }
        }

        // not in cart yet — add as a new item
        CartItem newItem = new CartItem();
        newItem.setCart(cart);
        newItem.setProduct(product);
        newItem.setQuantity(request.getQuantity());
        cart.getItems().add(newItem);

        cartRepository.save(cart);
        return ResponseEntity.ok(cart);
    }

    @GetMapping
    public ResponseEntity<?> viewCart(Authentication authentication) {
        Cart cart = getOrCreateCart(authentication.getName());
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long productId, Authentication authentication) {
        Cart cart = getOrCreateCart(authentication.getName());
        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cartRepository.save(cart);
        return ResponseEntity.ok(cart);
    }
}