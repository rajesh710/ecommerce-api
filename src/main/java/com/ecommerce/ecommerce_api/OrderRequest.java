package com.ecommerce.ecommerce_api;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private List<OrderItemRequest> items;
}