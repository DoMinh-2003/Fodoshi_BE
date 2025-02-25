package com.BE.repository;

import com.BE.enums.ProductStatus;
import com.BE.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByBrandsId(Long brandId);

    List<Product> findByCategoriesId(Long categoryId);

    List<Product> findAllByStatus(ProductStatus productStatus);
}