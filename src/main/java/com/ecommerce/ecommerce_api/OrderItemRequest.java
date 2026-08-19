package com.ecommerce.ecommerce_api;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long productId;
    private Integer quantity;
}