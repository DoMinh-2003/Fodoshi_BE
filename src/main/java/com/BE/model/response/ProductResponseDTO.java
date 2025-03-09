package com.BE.model.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ProductResponseDTO {
    public Long id;
    public String name;
    public String description;
    public String category;
    public String brand;
    public String condition;
    public String size;
    public BigDecimal originalPrice;
    public BigDecimal sellingPrice;
    public LocalDateTime createdAt;
    public List<String> tags;
    public List<String> imageUrls;
    public Long consignorId;
}
