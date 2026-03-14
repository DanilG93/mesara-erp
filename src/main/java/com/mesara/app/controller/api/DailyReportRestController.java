package com.mesara.app.controller.api;

import com.mesara.app.dto.DailyReportRequest;
import com.mesara.app.service.EntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports/daily")
@RequiredArgsConstructor
@Tag(name = "1. Daily Reports", description = "API for submitting and finalizing daily store inventory reports")
public class DailyReportRestController {

    private final EntryService entryService;

    @Operation(
            summary = "Submit Daily Report",
            description = "Receives a JSON payload containing store, date, revenue, and a list of product movements (received, sold, waste, returned) to update database stock levels."
    )
    @PostMapping
    public ResponseEntity<Void> saveDailyReport(@RequestBody DailyReportRequest request) {

        entryService.saveDailyReportFromRest(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Finalize Day",
            description = "Fills in zero-movements for all untouched active products for a specific store and date to ensure daily report completeness."
    )
    @PostMapping("/finalize")
    public ResponseEntity<Void> finalizeDay(@RequestParam Long storeId, @RequestParam LocalDate date) {
        entryService.finalizeDay(storeId,date);
        return ResponseEntity.ok().build();
    }
}
