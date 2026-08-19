package com.ecommerce.ecommerce_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest request, Authentication authentication) {

        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0.0;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId()).orElse(null);

            if (product == null) {
                return ResponseEntity.badRequest().body("Product not found: " + itemRequest.getProductId());
            }

            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                return ResponseEntity.badRequest().body("Not enough stock for: " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());

            total += product.getPrice() * itemRequest.getQuantity();

            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            productRepository.save(product);

            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        order.setTotalAmount(total);

        orderRepository.save(order);

        return ResponseEntity.ok(order);
    }

    @GetMapping
    public List<Order> getMyOrders(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        return orderRepository.findByUserId(user.getId());
    }
}