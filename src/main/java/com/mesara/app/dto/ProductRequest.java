package com.mesara.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Product name is required")
        String name,

        Long categoryId,
        String unit,

        @Positive(message = "Price must be greater than zero")
        BigDecimal price
) {
}
