package com.mesara.app.repository;

import com.mesara.app.domain.DailyStoreReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyStoreReportRepository extends JpaRepository<DailyStoreReport, Long> {
}