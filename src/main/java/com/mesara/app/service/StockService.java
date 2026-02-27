package com.mesara.app.service;

import com.mesara.app.domain.ProductStock;
import com.mesara.app.domain.Store;
import com.mesara.app.repository.DailyStoreReportRepository;
import com.mesara.app.repository.ProductStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductStockRepository stockRepository;
    private final StoreService storeService;
    private final DailyStoreReportRepository reportRepository;

    public List<ProductStock> getStockByStore(Long storeId) {
        if (storeId == null) {
            return stockRepository.findAll();
        }

        Store store = storeService.getById(storeId);
        return stockRepository.findByStore(store);
    }

    public Long getLatestStoreIdWithActivity() {
        return reportRepository.findTopByOrderByIdDesc()
                .map(report -> report.getStore().getId()) // Ako nađe izveštaj, uzmi ID radnje
                .orElse(null); // Ako je baza skroz prazna, vrati null
    }
}