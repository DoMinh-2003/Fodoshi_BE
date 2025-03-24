package com.BE.controller;

import com.BE.model.response.dashboard.*;
import com.BE.service.implementServices.AdminDashboardService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;

@RestController
@RequestMapping("api/admin/dashboard")
@SecurityRequirement(name = "api")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService dashboardService;
    
    @Autowired
    private ResponseHandler responseHandler;

    @GetMapping("/revenue")
    public ResponseEntity getRevenue(
            @RequestParam(required = false) String period, // day, week, month, year, or null for summary
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        // If no period specified, return summary
        if (period == null) {
            RevenueSummaryDTO summary = dashboardService.getRevenueSummary();
            return responseHandler.response(200, "Revenue summary fetched successfully", summary);
        }
        
        // Handle week period specifically
        if ("week".equalsIgnoreCase(period)) {
            LocalDate today = LocalDate.now();
            if (startDate == null) {
                startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            }
            
            if (endDate == null) {
                endDate = startDate.plusDays(6);
            }
        }
        
        RevenueByPeriodDTO revenue = dashboardService.getRevenueByPeriod(period, startDate, endDate);
        return responseHandler.response(200, period + " revenue fetched successfully", revenue);
    }
    
    @GetMapping("/products/summary")
    public ResponseEntity getProductsSummary() {
        ProductSummaryDTO summary = dashboardService.getProductsSummary();
        return responseHandler.response(200, "Products summary fetched successfully", summary);
    }
    
    @GetMapping("/products/sold")
    public ResponseEntity getProductsSold(
            @RequestParam String period, // day, month, year
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        ProductsSoldDTO soldProducts = dashboardService.getProductsSold(period, date);
        return responseHandler.response(200, "Products sold fetched successfully", soldProducts);
    }
    
    @GetMapping("/categories/count")
    public ResponseEntity getCategoriesCount() {
        int count = dashboardService.getCategoriesCount();
        return responseHandler.response(200, "Categories count fetched successfully", count);
    }
    
    @GetMapping("/brands/count")
    public ResponseEntity getBrandsCount() {
        int count = dashboardService.getBrandsCount();
        return responseHandler.response(200, "Brands count fetched successfully", count);
    }
    
    @GetMapping("/overview")
    public ResponseEntity getDashboardOverview() {
        DashboardOverviewDTO overview = dashboardService.getDashboardOverview();
        return responseHandler.response(200, "Dashboard overview fetched successfully", overview);
    }
}