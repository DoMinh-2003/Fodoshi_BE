package com.BE.controller;

import com.BE.model.entity.Product;
import com.BE.model.entity.User;
import com.BE.model.request.ProductRequestDTO;
import com.BE.model.request.ProductStatusRequest;
import com.BE.model.response.ProductResponseDTO;
import com.BE.repository.ProductRepository;
import com.BE.service.implementServices.ProductService;
import com.BE.utils.AccountUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name ="api")
@RequestMapping("api/products")
class ProductController {


    @Autowired
    private ProductService productService;

    @Autowired
    ProductRepository productRepository;
    @GetMapping
    public ResponseEntity getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }


    @GetMapping("/by-brand/{id}")
    public ResponseEntity getProductsByBrand(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductsByBrand(id));
    }

    @GetMapping("/by-category/{id}")
    public ResponseEntity getProductsByCategory(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductsByCategory(id));
    }


    @PatchMapping("/status")
    public ResponseEntity getProductByStatus(@Valid @RequestBody ProductStatusRequest statusRequest) {
        return ResponseEntity.ok(productService.getProductByStatus(statusRequest));
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.deleteProduct(id));
    }

    @GetMapping("{id}")
    public ResponseEntity getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity createProduct(@RequestBody ProductRequestDTO productDTO) {
        return ResponseEntity.ok(productService.createProduct(productDTO));
    }

    @PutMapping("{id}")
    public ResponseEntity updateProduct(@PathVariable Long id,@RequestBody ProductRequestDTO productDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }
}
