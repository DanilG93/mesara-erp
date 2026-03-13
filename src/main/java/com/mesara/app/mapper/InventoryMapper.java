package com.mesara.app.mapper;

import com.mesara.app.domain.ProductStock;
import com.mesara.app.dto.InventoryResponse;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryResponse toResponse(ProductStock stock) {
        return new InventoryResponse(
                stock.getId(),
                stock.getStore().getId(),
                stock.getStore().getName(),
                stock.getProduct().getId(),
                stock.getProduct().getName(),
                stock.getProduct().getUnit(),
                stock.getQuantity()
        );
    }
}
