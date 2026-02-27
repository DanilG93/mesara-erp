package com.mesara.app.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MovementRowDTO {
    private String productName;
    private BigDecimal received;
    private BigDecimal sold;
    private BigDecimal waste;
}