package com.BE.repository;


import com.BE.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByIsDeletedFalse();  // Lấy danh sách chưa bị xóa

}