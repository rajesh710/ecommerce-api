package com.ecommerce.ecommerce_api.service;

import com.ecommerce.ecommerce_api.dto.CartItemRequest;
import com.ecommerce.ecommerce_api.entity.Cart;
import com.ecommerce.ecommerce_api.entity.CartItem;
import com.ecommerce.ecommerce_api.entity.Product;
import com.ecommerce.ecommerce_api.entity.User;
import com.ecommerce.ecommerce_api.repository.CartRepository;
import com.ecommerce.ecommerce_api.repository.ProductRepository;
import com.ecommerce.ecommerce_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CartService {

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

    public Cart addToCart(CartItemRequest request, String username) {
        Cart cart = getOrCreateCart(username);
        Product product = productRepository.findById(request.getProductId()).orElse(null);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + request.getQuantity());
                return cartRepository.save(cart);
            }
        }

        CartItem newItem = new CartItem();
        newItem.setCart(cart);
        newItem.setProduct(product);
        newItem.setQuantity(request.getQuantity());
        cart.getItems().add(newItem);

        return cartRepository.save(cart);
    }

    public Cart viewCart(String username) {
        return getOrCreateCart(username);
    }

    public Cart removeFromCart(Long productId, String username) {
        Cart cart = getOrCreateCart(username);
        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        return cartRepository.save(cart);
    }
}