package com.BE.repository;

import com.BE.enums.ProductStatus;
import com.BE.model.entity.Product;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByBrandsId(Long brandId);

    List<Product> findByCategoriesId(Long categoryId);

    List<Product> findAllByStatus(ProductStatus productStatus);

    List<Product> findAllByConsignorId(UUID id);

    List<Product> findAllByIsDeleted(Boolean isDelete);


    // Tìm theo name, description, category
    List<Product> findByNameContainingIgnoreCaseOrCategories_NameContainingIgnoreCase(
            String nameKeyword, String categoryKeyword);

    // Tìm theo tag
    List<Product> findByTags_TagNameContainingIgnoreCase(String tagKeyword);







}