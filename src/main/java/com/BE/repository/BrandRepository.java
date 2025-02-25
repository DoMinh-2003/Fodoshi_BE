package com.BE.repository;

import com.BE.model.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    List<Brand> findAllByIsDeletedFalse();  // Lấy danh sách chưa bị xóa

}
