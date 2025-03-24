package com.BE.model.response.dashboard;

import lombok.Data;

@Data
public class CustomerSummaryDTO {
    private int totalRegisteredCustomers; // Tổng số khách hàng đã đăng ký
    private int totalGuestCustomers;      // Tổng số khách vãng lai
    private int totalCustomers;           // Tổng số tất cả khách hàng
    
    // Constructor để tính toán tổng số khách hàng
    public CustomerSummaryDTO() {
        this.totalCustomers = this.totalRegisteredCustomers + this.totalGuestCustomers;
    }
} 