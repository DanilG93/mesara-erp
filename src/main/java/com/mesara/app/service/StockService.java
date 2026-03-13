package com.mesara.app.service;

import com.mesara.app.domain.ProductStock;
import com.mesara.app.repository.ProductStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final ProductStockRepository stockRepository;
    private final StoreService storeService;
    private final EntryService entryService;

    public List<ProductStock> getStockByStore(Long storeId) {
        if (storeId == null) {
            return stockRepository.findAll();
        }

        return stockRepository.findByStoreId(storeId);
    }

    public Long resolveStoreId(Long requestedStoreId) {
        if (requestedStoreId != null) {
            return requestedStoreId;
        }

        Long latestId = entryService.getLatestStoreIdWithActivity();
        if (latestId != null) {
            return latestId;
        }

        var activeStores = storeService.getAllActive();
        if (!activeStores.isEmpty()) {
            return activeStores.getFirst().getId();
        }

        return null;
    }
}