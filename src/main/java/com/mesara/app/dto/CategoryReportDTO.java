package com.mesara.app.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryReportDTO {
    private String categoryName;
    private BigDecimal totalReceived;
    private BigDecimal totalSold;
    private BigDecimal totalWaste;
}