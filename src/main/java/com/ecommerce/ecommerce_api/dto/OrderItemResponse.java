package com.ecommerce.ecommerce_api.dto;

import lombok.Data;

@Data
public class OrderItemResponse {
    private String productName;
    private Integer quantity;
    private Double priceAtPurchase;
}