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
        order.setPaymentStatus("PENDING"); // add this line

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

        return ResponseEntity.ok(mapToResponse(order));
    }

    @GetMapping
    public List<OrderResponse> getMyOrders(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        List<Order> orders = orderRepository.findByUserId(user.getId());

        List<OrderResponse> responses = new ArrayList<>();
        for (Order order : orders) {
            responses.add(mapToResponse(order));
        }
        return responses;
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setOrderDate(order.getOrderDate());

        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setProductName(item.getProduct().getName());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setPriceAtPurchase(item.getPriceAtPurchase());
            itemResponses.add(itemResponse);
        }
        response.setItems(itemResponses);

        return response;
    }
    @PutMapping("/{id}/pay")
    public ResponseEntity<?> markAsPaid(@PathVariable Long id, Authentication authentication) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        order.setPaymentStatus("PAID");
        order.setStatus("COMPLETED");
        orderRepository.save(order);

        return ResponseEntity.ok(mapToResponse(order));
    }

}