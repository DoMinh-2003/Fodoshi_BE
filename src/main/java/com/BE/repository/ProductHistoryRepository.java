package com.BE.repository;

import com.BE.model.entity.ProductHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductHistoryRepository extends JpaRepository<ProductHistory,Long> {
}
