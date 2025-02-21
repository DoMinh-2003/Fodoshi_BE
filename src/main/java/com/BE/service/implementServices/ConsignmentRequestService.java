package com.BE.service.implementServices;

import com.BE.model.entity.ConsignmentRequest;
import com.BE.model.entity.User;
import com.BE.model.request.ConsignmentRequestDTO;
import com.BE.repository.ConsignmentRequestRepository;
import com.BE.repository.UserRepository;
import com.BE.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsignmentRequestService {
    @Autowired
    private ConsignmentRequestRepository consignmentRequestRepository;


    public List<ConsignmentRequest> getAllRequests() {
        return consignmentRequestRepository.findAll();
    }

    public ConsignmentRequest createRequest(ConsignmentRequestDTO requestDTO) {
        User user = AccountUtils.getCurrentUser();

        ConsignmentRequest request = new ConsignmentRequest();
        request.setUser(user);
        request.setProductName(requestDTO.productName);
        request.setCategory(requestDTO.category);
        request.setBrand(requestDTO.brand);
        request.setSizeLength(requestDTO.sizeLength);
        request.setSizeWidth(requestDTO.sizeWidth);
        request.setSizeWaist(requestDTO.sizeWaist);
        request.setImageUrls(requestDTO.imageUrls);
        request.setConsignmentMethod(requestDTO.consignmentMethod);
        request.setStatus(requestDTO.status);
        return consignmentRequestRepository.save(request);
    }
}