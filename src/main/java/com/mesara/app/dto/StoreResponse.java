package com.mesara.app.dto;

public record StoreResponse(
        Long id,
        String name,
        String address,
        String location,
        boolean active
) {
}
