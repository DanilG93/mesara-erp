package com.mesara.app.controller.api;


import com.mesara.app.dto.AnalyticsFilterRequest;
import com.mesara.app.dto.CategoryReportDTO;
import com.mesara.app.dto.MovementRowDTO;
import com.mesara.app.dto.StoreContributionDTO;
import com.mesara.app.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsRestController {

    private final ReportService reportService;

    @PostMapping("/advanced")
    public ResponseEntity<List<MovementRowDTO>> getAdvancedReport(@RequestBody AnalyticsFilterRequest request) {
        List<MovementRowDTO> report = reportService.getAdvancedReport(
                request.storeIds(),
                request.productIds(),
                request.startDate(),
                request.endDate()
        );

        return ResponseEntity.ok(report);
    }

    @PostMapping("/contributions")
    public ResponseEntity<List<StoreContributionDTO>> getStoreContributions(@RequestBody AnalyticsFilterRequest request) {
        List<StoreContributionDTO> contributions = reportService.getStoreContributions(
                request.storeIds(),
                request.productIds(),
                request.startDate(),
                request.endDate()
        );

        return ResponseEntity.ok(contributions);
    }

    @PostMapping("/categories")
    public ResponseEntity<List<CategoryReportDTO>> getCategoryTurnover(@RequestBody AnalyticsFilterRequest request) {
        List<CategoryReportDTO> turnover = reportService.getCategoryTurnover(
                request.storeIds(),
                request.productIds(),
                request.startDate(),
                request.endDate()
        );
        return ResponseEntity.ok(turnover);
    }

}
