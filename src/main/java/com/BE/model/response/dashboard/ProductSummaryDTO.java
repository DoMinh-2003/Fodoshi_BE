package com.BE.model.response.dashboard;

import lombok.Data;

@Data
public class ProductSummaryDTO {
    private long totalProducts;
    private long availableProducts;
    private long soldProducts;
}