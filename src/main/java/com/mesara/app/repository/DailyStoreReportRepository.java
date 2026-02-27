package com.mesara.app.repository;

import com.mesara.app.domain.DailyStoreReport;
import com.mesara.app.domain.StockMovement;
import com.mesara.app.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyStoreReportRepository extends JpaRepository<DailyStoreReport, Long> {
    Optional<DailyStoreReport> findByStoreAndReportDate(Store store, LocalDate reportDate);
    Optional<DailyStoreReport> findTopByOrderByIdDesc();
}