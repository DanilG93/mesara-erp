package com.mesara.app.dto;

import java.time.LocalDate;
import java.util.List;

public record AnalyticsFilterRequest(
        List<Long> storeIds,
        List<Long> productIds,
        LocalDate startDate,
        LocalDate endDate
) {
}
