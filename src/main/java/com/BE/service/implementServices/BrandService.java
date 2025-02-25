package com.BE.service.implementServices;


import com.BE.exception.exceptions.NotFoundException;
import com.BE.model.entity.Brand;
import com.BE.model.request.BrandRequest;
import com.BE.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> getAllBrandsByDeletedFalse() {
        return brandRepository.findAllByIsDeletedFalse();
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Brand getBrandById(Long id) {
        return brandRepository.findById(id).orElseThrow(() -> new NotFoundException("Brand not found"));
    }

    public Brand createBrand(BrandRequest brandRequest) {
        Brand brand = new Brand();
        brand.setName(brandRequest.getName());
        brand.setImage(brandRequest.getImage());
        try {
            return brandRepository.save(brand);
        }catch (DataIntegrityViolationException e){
            throw new DataIntegrityViolationException("Duplicate Brand Name");
        }

    }

    public Brand updateBrand(Long id, BrandRequest brandRequest) {
        return brandRepository.findById(id).map(brand -> {
            brand.setName(brandRequest.getName());
            brand.setImage(brandRequest.getImage());
            return brandRepository.save(brand);
        }).orElseThrow(() -> new RuntimeException("Brand not found"));
    }

    public Brand deleteBrand(Long id) {
        Brand brand =  getBrandById(id);
        brand.setIsDeleted(true);
        return brandRepository.save(brand);
    }
}