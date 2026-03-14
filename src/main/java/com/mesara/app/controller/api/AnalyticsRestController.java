package com.mesara.app.controller.api;


import com.mesara.app.dto.AnalyticsFilterRequest;
import com.mesara.app.dto.CategoryReportDTO;
import com.mesara.app.dto.MovementRowDTO;
import com.mesara.app.dto.StoreContributionDTO;
import com.mesara.app.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "2. Analytics & Reporting", description = "API for retrieving advanced business reports and charts")
public class AnalyticsRestController {

    private final ReportService reportService;

    @Operation(
            summary = "Main Movement Table",
            description = "Returns summary movements (received, sold, waste, returned) for selected products within a given date range."
    )
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

    @Operation(
            summary = "Store Contributions",
            description = "Returns data on which store sold or wasted the most for the given filters (ideal for pie charts)."
    )
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

    @Operation(
            summary = "Category Turnover",
            description = "Grouped sales by product categories like Pork or Beef for a given period (ideal for bar charts)."
    )
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
