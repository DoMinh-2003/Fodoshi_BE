package com.BE.controller;

import com.BE.model.request.ProductStatusRequest;
import com.BE.service.implementServices.ProductService;
import com.BE.service.implementServices.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name ="api")
@RequestMapping("api/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    @GetMapping("/phone/{phone}")
    public ResponseEntity getProductByStatus(@PathVariable String phone) {
        return ResponseEntity.ok(userService.getUserByPhone(phone));
    }

    @GetMapping("/products")
    public ResponseEntity getProductByConsignor() {
        return ResponseEntity.ok(productService.getProductByConsignor());
    }

}
