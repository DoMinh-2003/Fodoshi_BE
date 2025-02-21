package com.BE.model.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter
public class ProductRequestDTO {
    public String name;
    public String description;
    public String category;
    public String brand;
    public String condition;
    public String size;
    public BigDecimal originalPrice;
    public BigDecimal sellingPrice;
    public List<String> tags;
    public List<String> imageUrls;
    public Long consignorId;
}
