package com.mesara.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class StoreContributionDTO {
    private String productName;
    private String storeName;
    private BigDecimal received;
    private BigDecimal sold;
    private BigDecimal waste;
}