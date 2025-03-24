package com.BE.model.response.dashboard;

import lombok.Data;

@Data
public class ProductsSoldDTO {
    private String periodType; // day, month, year
    private int count;
    private String label;
}