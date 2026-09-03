package com.ecommerce.ecommerce_api.controller;

import com.ecommerce.ecommerce_api.dto.OrderRequest;
import com.ecommerce.ecommerce_api.dto.OrderResponse;
import com.ecommerce.ecommerce_api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest request, Authentication authentication) {
        try {
            return ResponseEntity.ok(orderService.placeOrder(request, authentication.getName()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public List<OrderResponse> getMyOrders(Authentication authentication) {
        return orderService.getMyOrders(authentication.getName());
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<?> markAsPaid(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.markAsPaid(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}