package com.BE.controller;

import com.BE.model.entity.Product;
import com.BE.model.entity.User;
import com.BE.model.request.ProductRequestDTO;
import com.BE.model.response.ProductResponseDTO;
import com.BE.repository.ProductRepository;
import com.BE.service.implementServices.ProductService;
import com.BE.utils.AccountUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name ="api")
@RequestMapping("/products")
class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    ProductRepository productRepository;
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping
    public Product createProduct(@RequestBody ProductRequestDTO productDTO) {
        User consignor = AccountUtils.getCurrentUser();
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setCategory(productDTO.getCategory());
        product.setBrand(productDTO.getBrand());
        product.setProductCondition(productDTO.getCondition());
        product.setSize(productDTO.getSize());
        product.setImageUrls(productDTO.getImageUrls());
        product.setOriginalPrice(productDTO.getOriginalPrice());
        product.setSellingPrice(productDTO.getSellingPrice());
        product.setConsignor(consignor);
        return productRepository.save(product);
    }
}
