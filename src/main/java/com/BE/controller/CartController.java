package com.BE.controller;


import com.BE.model.request.AddToCartRequest;
import com.BE.model.response.CartResponse;
import com.BE.service.implementServices.CartService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/cart")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class CartController {
    @Autowired
    CartService cartService;

    @Autowired
    ResponseHandler responseHandler;

    @GetMapping
    public ResponseEntity getDetail() {
        CartResponse cartResponse = cartService.getDetail();
        return responseHandler.response(200, "Get Cart Successfully!", cartResponse);
    }

    @PostMapping
    public ResponseEntity created( @RequestBody AddToCartRequest addToCartRequest) {
        return responseHandler.response(200, "Add To Cart Successfully!", cartService.created(addToCartRequest));
    }

    @DeleteMapping("{id}")
    public ResponseEntity update(@Valid @PathVariable @NotNull UUID id) {
        return responseHandler.response(200, "Delete To Cart Successfully!", cartService.delete(id));
    }



}
