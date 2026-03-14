package com.mesara.app.controller.api;

import com.mesara.app.dto.DailyReportRequest;
import com.mesara.app.service.EntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports/daily")
@RequiredArgsConstructor
public class DailyReportRestController {

    private final EntryService entryService;

    @PostMapping
    public ResponseEntity<Void> saveDailyReport(@RequestBody DailyReportRequest request) {

        entryService.saveDailyReportFromRest(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/finalize")
    public ResponseEntity<Void> finalizeDay(@RequestParam Long storeId, @RequestParam LocalDate date) {
        entryService.finalizeDay(storeId,date);
        return ResponseEntity.ok().build();
    }
}
