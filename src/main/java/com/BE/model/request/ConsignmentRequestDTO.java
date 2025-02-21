package com.BE.model.request;

import com.BE.enums.ConsignmentStatus;

import java.util.List;

public class ConsignmentRequestDTO {
    public String productName;
    public String category;
    public String brand;
    public String sizeLength;
    public String sizeWidth;
    public String sizeWaist;
    public List<String> imageUrls;
    public String consignmentMethod;
    public ConsignmentStatus status;
}
