package com.ecommerce.ecommerce_api;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime orderDate;

    private Double totalAmount;

    private String status; // e.g., "PENDING", "COMPLETED", "CANCELLED"

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
}