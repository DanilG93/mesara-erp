package com.mesara.app.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String categoryName,
        String unit,
        BigDecimal price,
        boolean active
) {
}
