package com.BE.service.implementServices;

import com.BE.exception.exceptions.NotFoundException;
import com.BE.model.entity.Category;
import com.BE.model.request.CategoryRequest;
import com.BE.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getAllByIsDeletedFalse() {
        return categoryRepository.findAllByIsDeletedFalse();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
    }

    public Category createCategory(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setImage(categoryRequest.getImage());
        try {
            return categoryRepository.save(category);
        }catch (DataIntegrityViolationException e){
            throw new DataIntegrityViolationException("Duplicate Category Name");
        }
    }

    public Category updateCategory(Long id, CategoryRequest categoryRequest) {
        return categoryRepository.findById(id).map(category -> {
            category.setName(categoryRequest.getName());
            category.setImage(categoryRequest.getImage());
            return categoryRepository.save(category);
        }).orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public Category deleteCategory(Long id) {
        Category category = getCategoryById(id);
        return categoryRepository.save(category);
    }
}