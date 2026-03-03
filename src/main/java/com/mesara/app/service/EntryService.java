package com.mesara.app.service;

import com.mesara.app.domain.*;
import com.mesara.app.dto.MovementRowDTO;
import com.mesara.app.repository.DailyStoreReportRepository;
import com.mesara.app.repository.ProductStockRepository;
import com.mesara.app.repository.StockMovementRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EntryService {

    private final DailyStoreReportRepository reportRepository;
    private final StockMovementRepository movementRepository;
    private final ProductStockRepository stockRepository;
    private final StoreService storeService;
    private final ProductService productService;

    public DailyStoreReport findReportByStoreAndDate(Store store, LocalDate date) {
        return reportRepository.findByStoreAndReportDate(store, date).orElse(null);
    }

    public Map<Long, MovementRowDTO> getMovementMap(DailyStoreReport report) {
        List<StockMovement> movements = movementRepository.findByReport(report);

        return movements.stream()
                .collect(Collectors.groupingBy(m -> m.getProduct().getId()))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            List<StockMovement> productMovements = entry.getValue();

                            // Nalazimo maxId za DTO
                            Long maxId = productMovements.stream()
                                    .mapToLong(StockMovement::getId)
                                    .max().orElse(0L);

                            BigDecimal received = productMovements.stream()
                                    .filter(m -> m.getType() == MovementType.PURCHASE)
                                    .map(StockMovement::getQuantity)
                                    .reduce(BigDecimal::add).orElse(null);

                            BigDecimal sold = productMovements.stream()
                                    .filter(m -> m.getType() == MovementType.SALE)
                                    .map(StockMovement::getQuantity)
                                    .reduce(BigDecimal::add).orElse(null);

                            BigDecimal waste = productMovements.stream()
                                    .filter(m -> m.getType() == MovementType.WASTE)
                                    .map(StockMovement::getQuantity)
                                    .reduce(BigDecimal::add).orElse(null);

                            return new MovementRowDTO(productMovements.get(0).getProduct().getName(),
                                    received, sold, waste, maxId);
                        }
                ));
    }

    @Transactional
    public void saveDailyReport(Long storeId, LocalDate reportDate, BigDecimal totalRevenue, String note,
                                List<Long> productIds, List<BigDecimal> received,
                                List<BigDecimal> sold, List<BigDecimal> waste) {

        Store store = storeService.getById(storeId);
        DailyStoreReport report = reportRepository.findByStoreAndReportDate(store, reportDate)
                .orElse(new DailyStoreReport());

        if (report.getId() == null) {
            report.setStore(store);
            report.setReportDate(reportDate);
        }

        if (totalRevenue != null) {
            report.setTotalRevenue(totalRevenue);
        }

        if (note != null && !note.isEmpty()) {
            report.setNote(note);
        }

        report = reportRepository.save(report);

        if (productIds != null) {
            for (int i = 0; i < productIds.size(); i++) {
                Product product = productService.getById(productIds.get(i));

                BigDecimal r = (received != null && i < received.size()) ? received.get(i) : null;
                BigDecimal s = (sold != null && i < sold.size()) ? sold.get(i) : null;
                BigDecimal w = (waste != null && i < waste.size()) ? waste.get(i) : null;

                processMovement(product, store, r, MovementType.PURCHASE, report);
                processMovement(product, store, s, MovementType.SALE, report);
                processMovement(product, store, w, MovementType.WASTE, report);
            }
        }
    }

    private void processMovement(Product p, Store s, BigDecimal qty, MovementType type, DailyStoreReport report) {
        if (qty != null && qty.compareTo(BigDecimal.ZERO) >= 0) {

            // IDEMPOTENCY CHECK: Provera da li već postoji ovaj unos da ne bismo duplirali lager
            boolean alreadyExists = movementRepository.findByReport(report).stream()
                    .anyMatch(m -> m.getProduct().getId().equals(p.getId()) && m.getType() == type);

            if (!alreadyExists) {
                // A. Snimanje u ISTORIJU
                StockMovement m = new StockMovement();
                m.setProduct(p);
                m.setStore(s);
                m.setQuantity(qty);
                m.setType(type);
                m.setReport(report);
                movementRepository.save(m);

                // B. Ažuriranje TRENUTNOG LAGERA
                if (qty.compareTo(BigDecimal.ZERO) > 0) {
                    ProductStock stock = stockRepository.findByStoreAndProduct(s, p)
                            .orElse(new ProductStock());

                    if (stock.getId() == null) {
                        stock.setStore(s);
                        stock.setProduct(p);
                        stock.setQuantity(BigDecimal.ZERO);
                    }

                    if (type == MovementType.PURCHASE) {
                        stock.setQuantity(stock.getQuantity().add(qty));
                    } else {
                        stock.setQuantity(stock.getQuantity().subtract(qty));
                    }
                    stockRepository.save(stock);
                }
            }
        }
    }

    public List<MovementRowDTO> getGroupedMovements(DailyStoreReport report) {
        List<StockMovement> movements = movementRepository.findByReport(report);

        return movements.stream()
                .collect(Collectors.groupingBy(m -> m.getProduct().getId()))
                .values().stream()
                .map(productMovements -> {
                    String name = productMovements.get(0).getProduct().getName();

                    Long maxId = productMovements.stream()
                            .mapToLong(StockMovement::getId)
                            .max().orElse(0L);

                    BigDecimal received = productMovements.stream()
                            .filter(m -> m.getType() == MovementType.PURCHASE)
                            .map(StockMovement::getQuantity)
                            .reduce(BigDecimal::add).orElse(null);

                    BigDecimal sold = productMovements.stream()
                            .filter(m -> m.getType() == MovementType.SALE)
                            .map(StockMovement::getQuantity)
                            .reduce(BigDecimal::add).orElse(null);

                    BigDecimal waste = productMovements.stream()
                            .filter(m -> m.getType() == MovementType.WASTE)
                            .map(StockMovement::getQuantity)
                            .reduce(BigDecimal::add).orElse(null);

                    return new MovementRowDTO(name, received, sold, waste, maxId);
                })
                // SORTIRANJE: Poslednji ID (najnoviji unos) ide na vrh
                .sorted(Comparator.comparing(MovementRowDTO::getLastId).reversed())
                .collect(Collectors.toList());
    }

    @Transactional
    public void finalizeDay(Long storeId, LocalDate reportDate) {
        Store store = storeService.getById(storeId);
        DailyStoreReport report = reportRepository.findByStoreAndReportDate(store, reportDate)
                .orElseThrow(() -> new RuntimeException("Izvještaj mora postojati."));

        Map<Long, MovementRowDTO> movementMap = getMovementMap(report);
        List<Product> activeProducts = productService.getAllActive();

        for (Product product : activeProducts) {
            MovementRowDTO currentStatus = movementMap.get(product.getId());

            if (currentStatus == null) {
                processMovement(product, store, BigDecimal.ZERO, MovementType.PURCHASE, report);
                processMovement(product, store, BigDecimal.ZERO, MovementType.SALE, report);
                processMovement(product, store, BigDecimal.ZERO, MovementType.WASTE, report);
            } else {
                if (currentStatus.getReceived() == null)
                    processMovement(product, store, BigDecimal.ZERO, MovementType.PURCHASE, report);
                if (currentStatus.getSold() == null)
                    processMovement(product, store, BigDecimal.ZERO, MovementType.SALE, report);
                if (currentStatus.getWaste() == null)
                    processMovement(product, store, BigDecimal.ZERO, MovementType.WASTE, report);
            }
        }
    }
}