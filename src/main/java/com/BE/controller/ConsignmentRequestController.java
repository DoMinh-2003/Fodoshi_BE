package com.BE.controller;

import com.BE.model.entity.ConsignmentRequest;
import com.BE.model.request.ConsignmentRequestDTO;
import com.BE.model.response.ConsignmentResponseDTO;
import com.BE.service.implementServices.ConsignmentRequestService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name ="api")
@RequestMapping("/consignment-requests")
class ConsignmentRequestController {
    @Autowired
    private ConsignmentRequestService consignmentRequestService;

    @GetMapping
    public List<ConsignmentRequest> getAllRequests() {
        return consignmentRequestService.getAllRequests();
    }

    @PostMapping
    public ConsignmentResponseDTO createRequest(@RequestBody ConsignmentRequestDTO requestDTO) {
        ConsignmentRequest request = consignmentRequestService.createRequest(requestDTO);
        ConsignmentResponseDTO response = new ConsignmentResponseDTO();
        response.id = request.getId();
        response.productName = request.getProductName();
        response.category = request.getCategory();
        response.brand = request.getBrand();
        response.sizeLength = request.getSizeLength();
        response.sizeWidth = request.getSizeWidth();
        response.sizeWaist = request.getSizeWaist();
        response.imageUrls = request.getImageUrls();
        response.consignmentMethod = request.getConsignmentMethod();
        response.status = request.getStatus().name();
        return response;
    }
}
