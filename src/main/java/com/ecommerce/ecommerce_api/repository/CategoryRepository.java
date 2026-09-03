package com.ecommerce.ecommerce_api.repository;

import com.ecommerce.ecommerce_api.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}