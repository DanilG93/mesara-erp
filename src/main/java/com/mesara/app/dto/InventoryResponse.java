package com.mesara.app.dto;

import java.math.BigDecimal;

public record InventoryResponse(
        Long id,
        Long storeId,
        String storeName,
        Long productId,
        String productName,
        String unit,
        BigDecimal quantity
) {
}
