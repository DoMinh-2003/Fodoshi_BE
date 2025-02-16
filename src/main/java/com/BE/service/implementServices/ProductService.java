package com.BE.service.implementServices;

import com.BE.model.entity.Product;
import com.BE.model.entity.User;
import com.BE.model.request.ProductRequestDTO;
import com.BE.repository.ProductRepository;
import com.BE.repository.UserRepository;
import com.BE.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product createProduct(ProductRequestDTO productDTO) {
        User user = AccountUtils.getCurrentUser();
        Product product = new Product();
        product.setName(productDTO.name);
        product.setDescription(productDTO.description);
        product.setCategory(productDTO.category);
        product.setBrand(productDTO.brand);
        product.setProductCondition(productDTO.condition);
        product.setSize(productDTO.size);
        product.setImageUrls(productDTO.imageUrls);
        product.setOriginalPrice(productDTO.originalPrice);
        product.setSellingPrice(productDTO.sellingPrice);
        product.setConsignor(user);
        return productRepository.save(product);
    }
}
