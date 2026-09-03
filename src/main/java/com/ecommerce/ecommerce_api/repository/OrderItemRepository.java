package com.ecommerce.ecommerce_api.repository;

import com.ecommerce.ecommerce_api.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}