package com.mesara.app.dto;

import jakarta.validation.constraints.NotBlank;

public record StoreRequest(
        @NotBlank(message = "Store name is required")
        String name,
        String address,
        String location
) {
}
