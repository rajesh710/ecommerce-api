package com.ecommerce.ecommerce_api.service;

import com.ecommerce.ecommerce_api.dto.*;
import com.ecommerce.ecommerce_api.entity.*;
import com.ecommerce.ecommerce_api.repository.OrderRepository;
import com.ecommerce.ecommerce_api.repository.ProductRepository;
import com.ecommerce.ecommerce_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public OrderResponse placeOrder(OrderRequest request, String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setPaymentStatus("PENDING");

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0.0;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId()).orElse(null);
            if (product == null) {
                throw new IllegalArgumentException("Product not found: " + itemRequest.getProductId());
            }

            int rowsUpdated = productRepository.decreaseStock(product.getId(), itemRequest.getQuantity());
            if (rowsUpdated == 0) {
                throw new IllegalStateException("Not enough stock for: " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());

            total += product.getPrice() * itemRequest.getQuantity();
            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        order.setTotalAmount(total);
        orderRepository.save(order);

        return mapToResponse(order);
    }

    public List<OrderResponse> getMyOrders(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        List<Order> orders = orderRepository.findByUserId(user.getId());
        List<OrderResponse> responses = new ArrayList<>();
        for (Order order : orders) {
            responses.add(mapToResponse(order));
        }
        return responses;
    }

    public OrderResponse markAsPaid(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }
        order.setPaymentStatus("PAID");
        order.setStatus("COMPLETED");
        orderRepository.save(order);
        return mapToResponse(order);
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
}