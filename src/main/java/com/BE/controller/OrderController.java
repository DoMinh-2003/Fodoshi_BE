package com.BE.controller;


import com.BE.model.request.OrderRequest;
import com.BE.model.request.OrderStatusRequest;
import com.BE.service.implementServices.OrderService;
import com.BE.utils.ResponseHandler;

import io.grpc.internal.JsonUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/order")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    ResponseHandler responseHandler;

//    @PostMapping
//    public ResponseEntity created(@Valid @RequestBody OrderRequest orderRequest) {
//        return responseHandler.response(200, "Created Order Successfully!", orderService.created(orderRequest));
//    }

    @GetMapping("{id}")
    public ResponseEntity getdetail(@PathVariable UUID id) {
        return responseHandler.response(200, "Get OrderDetail Successfully!", orderService.getdetail(id));
    }
    @GetMapping("/guess/{id}")
    public ResponseEntity getdetailGuess(@PathVariable UUID id) {
        return responseHandler.response(200, "Get OrderDetail Successfully!", orderService.getdetail(id));
    }

    @GetMapping
    public ResponseEntity getAll() {
        return responseHandler.response(200, "Get All Order Successfully!", orderService.getAll());
    }

    // Chỉ cần một tham số searchTerm (số điện thoại hoặc email)
    @GetMapping("/phone-email")
    public ResponseEntity getOrdersByPhoneOrEmail(@RequestParam String searchTerm) {

        return responseHandler.response(200, "Get Order By Phone Or Email Successfully!", orderService.getOrdersByPhoneOrEmail(searchTerm));

    }

    @GetMapping("/account")
    public ResponseEntity getByAccount() {
        return responseHandler.response(200, "Get Order By Account Successfully!", orderService.getByAccount());
    }

    @PatchMapping("{id}/status")
    public ResponseEntity changeStatus(@PathVariable UUID id, @Valid @RequestBody OrderStatusRequest statusRequest) {
        return responseHandler.response(200, "Change Order Status Successfully!", orderService.changeStatus(id,statusRequest));
    }

    @PatchMapping("status/guest/{id}")
    public ResponseEntity changeGuestStatus(
            @PathVariable UUID id, 
            @Valid @RequestBody OrderStatusRequest statusRequest,
            @RequestParam(required = false) String guestEmail) {
        return responseHandler.response(
            200, 
            "Change Guest Order Status Successfully!",
            orderService.changeGuestOrderStatus(id, statusRequest, guestEmail)
        );
    }





}
