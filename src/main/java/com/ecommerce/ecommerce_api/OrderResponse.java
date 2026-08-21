package com.ecommerce.ecommerce_api;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String status;
    private Double totalAmount;
    private LocalDateTime orderDate;
    private List<OrderItemResponse> items;
}