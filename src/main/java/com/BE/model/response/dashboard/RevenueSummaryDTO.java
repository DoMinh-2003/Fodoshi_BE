package com.BE.model.response.dashboard;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RevenueSummaryDTO {
    private BigDecimal todayRevenue = BigDecimal.ZERO;
    private BigDecimal monthRevenue = BigDecimal.ZERO;
    private BigDecimal yearRevenue = BigDecimal.ZERO;
    private BigDecimal totalRevenue = BigDecimal.ZERO;
}