package com.mesara.app.repository;

import com.mesara.app.domain.DailyStoreReport;
import com.mesara.app.domain.StockMovement;
import com.mesara.app.dto.CategoryReportDTO;
import com.mesara.app.dto.MovementRowDTO;
import com.mesara.app.dto.StoreContributionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByReport(DailyStoreReport report);

    @Query("SELECT new com.mesara.app.dto.MovementRowDTO(p.name, " +
            "SUM(CASE WHEN sm.type = 'PURCHASE' THEN sm.quantity ELSE 0 END), " +
            "SUM(CASE WHEN sm.type = 'SALE' THEN sm.quantity ELSE 0 END), " +
            "SUM(CASE WHEN sm.type = 'WASTE' THEN sm.quantity ELSE 0 END)) " +
            "FROM StockMovement sm " +
            "JOIN sm.product p " +
            "JOIN sm.report r " +
            "WHERE sm.store.id IN :storeIds " +
            "AND p.id IN :productIds " +
            "AND r.reportDate BETWEEN :start AND :end " +
            "GROUP BY p.name " +
            "ORDER BY SUM(CASE WHEN sm.type = 'SALE' THEN sm.quantity ELSE 0 END) DESC")
    List<MovementRowDTO> getAdvancedReport(
            @Param("storeIds") List<Long> storeIds,
            @Param("productIds") List<Long> productIds,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);


    @Query("SELECT new com.mesara.app.dto.StoreContributionDTO(p.name, s.name, " +
            "SUM(CASE WHEN sm.type = 'PURCHASE' THEN sm.quantity ELSE 0 END), " +
            "SUM(CASE WHEN sm.type = 'SALE' THEN sm.quantity ELSE 0 END), " +
            "SUM(CASE WHEN sm.type = 'WASTE' THEN sm.quantity ELSE 0 END)) " +
            "FROM StockMovement sm JOIN sm.product p JOIN sm.store s JOIN sm.report r " +
            "WHERE s.id IN :storeIds AND p.id IN :productIds AND r.reportDate BETWEEN :start AND :end " +
            "GROUP BY p.name, s.name")
    List<StoreContributionDTO> getContributionData(
            @Param("storeIds") List<Long> storeIds,
            @Param("productIds") List<Long> productIds,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);


    @Query("SELECT new com.mesara.app.dto.CategoryReportDTO(p.category.name, " +
            "SUM(CASE WHEN sm.type = 'PURCHASE' THEN sm.quantity ELSE 0 END), " +
            "SUM(CASE WHEN sm.type = 'SALE' THEN sm.quantity ELSE 0 END), " +
            "SUM(CASE WHEN sm.type = 'WASTE' THEN sm.quantity ELSE 0 END)) " +
            "FROM StockMovement sm JOIN sm.product p JOIN sm.report r " +
            "WHERE sm.store.id IN :storeIds AND p.id IN :productIds AND r.reportDate BETWEEN :start AND :end " +
            "GROUP BY p.category.name")
    List<CategoryReportDTO> getCategoryTurnover(
            @Param("storeIds") List<Long> storeIds,
            @Param("productIds") List<Long> productIds,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

}