package com.BE.model.response.dashboard;

import lombok.Data;

@Data
public class DashboardOverviewDTO {
    private RevenueSummaryDTO revenueSummary;
    private ProductSummaryDTO productSummary;
    private ProductsSoldDTO todaySoldProducts;
    private ProductsSoldDTO monthSoldProducts;
    private ProductsSoldDTO yearSoldProducts;
    private int categoriesCount;
    private int brandsCount;
    private CustomerSummaryDTO customerSummary;
}