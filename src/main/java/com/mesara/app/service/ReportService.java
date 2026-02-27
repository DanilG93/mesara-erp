package com.mesara.app.service;

import com.mesara.app.dto.CategoryReportDTO;
import com.mesara.app.dto.MovementRowDTO;
import com.mesara.app.dto.StoreContributionDTO;
import com.mesara.app.repository.StockMovementRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class ReportService {

    private final StockMovementRepository movementRepository;


    public List<MovementRowDTO> getAdvancedReport(List<Long> storeIds, List<Long> productIds, LocalDate start, LocalDate end) {

        if (storeIds == null || storeIds.isEmpty() || productIds == null || productIds.isEmpty()) {
            return List.of();
        }

        return movementRepository.getAdvancedReport(storeIds, productIds, start, end);
    }


    public List<StoreContributionDTO> getStoreContributions(List<Long> storeIds, List<Long> productIds, LocalDate start, LocalDate end) {
        return movementRepository.getContributionData(storeIds, productIds, start, end);
    }

    public List<CategoryReportDTO> getCategoryTurnover(List<Long> storeIds, List<Long> productIds, LocalDate start, LocalDate end) {
        return movementRepository.getCategoryTurnover(storeIds, productIds, start, end);
    }

}