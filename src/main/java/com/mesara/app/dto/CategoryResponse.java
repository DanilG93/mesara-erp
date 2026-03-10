package com.mesara.app.dto;

public record CategoryResponse(
        Long id,
        String name,
        boolean active
) {
}
