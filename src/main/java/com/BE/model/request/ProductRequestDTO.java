package com.BE.model.request;

import com.BE.enums.Gender;
import com.BE.enums.ProductStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ProductRequestDTO {
    public String name;
    public String description;
    public List<Long> category;
    public List<Long> brand;
    public String condition;
    public String size;
    private String color;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    public BigDecimal originalPrice;
    public BigDecimal sellingPrice;
    @Enumerated(EnumType.STRING)
    ProductStatus productStatus;
    public List<String> tags;
    public List<String> imageUrls;
    public UUID consignorId;
}
