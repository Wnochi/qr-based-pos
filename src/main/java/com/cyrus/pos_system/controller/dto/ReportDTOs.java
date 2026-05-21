package com.cyrus.pos_system.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReportDTOs {
    
    public record MonthlyReportResponse(
        LocalDate date,
        String transactionNo,
        String productName,
        int quantity,
        BigDecimal amount
    ) {}

    public record YearlyReportResponse(
        String month,
        BigDecimal revenue,
        long itemsSold
    ) {}
}