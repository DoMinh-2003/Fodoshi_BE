package com.BE.controller;

import com.BE.model.entity.Brand;
import com.BE.model.request.BrandRequest;
import com.BE.service.implementServices.BrandService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@SecurityRequirement(name ="api")
@RequestMapping("/api/brands")
public class BrandController {
    @Autowired
    private BrandService brandService;

    @GetMapping
    public ResponseEntity getAllBrands() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }

    @GetMapping("/active")
    public ResponseEntity getAllBrandsByDeletedFalse() {
        return ResponseEntity.ok(brandService.getAllBrandsByDeletedFalse());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable Long id) {
        Brand brand = brandService.getBrandById(id);
        return ResponseEntity.ok(brand);
    }

    @PostMapping
    public Brand createBrand(@RequestBody BrandRequest brandRequest) {
        return brandService.createBrand(brandRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Brand> updateBrand(@PathVariable Long id, @RequestBody BrandRequest brandRequest) {
        return ResponseEntity.ok(brandService.updateBrand(id, brandRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteBrand(@PathVariable Long id) {

        return ResponseEntity.ok(brandService.deleteBrand(id));
    }
}
