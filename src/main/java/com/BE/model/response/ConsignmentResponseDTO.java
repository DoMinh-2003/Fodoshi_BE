package com.BE.model.response;

import java.util.List;

public class ConsignmentResponseDTO {
    public Long id;
    public Long userId;
    public String productName;
    public String category;
    public String brand;
    public String sizeLength;
    public String sizeWidth;
    public String sizeWaist;
    public List<String> imageUrls;
    public String consignmentMethod;
    public String status;
}
