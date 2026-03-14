package com.mesara.app.dto;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


public record DailyReportRequest(
        Long storeId,
        LocalDate reportDate,
        BigDecimal totalRevenue,
        String note,
        List<MovementItemDTO> items
) {
    public record MovementItemDTO(
            Long productId,
            BigDecimal received,
            BigDecimal sold,
            BigDecimal waste,
            BigDecimal returned
    ) {
    }
}