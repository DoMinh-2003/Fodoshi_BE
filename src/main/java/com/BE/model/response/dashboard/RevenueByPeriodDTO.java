package com.BE.model.response.dashboard;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class RevenueByPeriodDTO {
    private String periodType; // day, month, year
    private Map<String, BigDecimal> revenueData;
}