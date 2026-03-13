package com.mesara.app.controller.api;

import com.mesara.app.dto.InventoryResponse;
import com.mesara.app.mapper.InventoryMapper;
import com.mesara.app.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryRestController {

    private final StockService stockService;
    private final InventoryMapper inventoryMapper;

    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getInventory(@RequestParam(required = false) Long storeId) {

        Long resolvedStoreId = stockService.resolveStoreId(storeId);

        if (resolvedStoreId == null) {
            return ResponseEntity.ok(List.of());
        }

        List<InventoryResponse> inventoryData = stockService.getStockByStore(resolvedStoreId)
                .stream().map(inventoryMapper::toResponse)
                .toList();

        return ResponseEntity.ok(inventoryData);
    }

}
