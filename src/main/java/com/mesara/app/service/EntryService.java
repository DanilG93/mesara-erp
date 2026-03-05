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

                            // NOVO: Dodat povrat
                            BigDecimal returned = productMovements.stream()
                                    .filter(m -> m.getType() == MovementType.RETURN)
                                    .map(StockMovement::getQuantity)
                                    .reduce(BigDecimal::add).orElse(null);

                            return new MovementRowDTO(productMovements.getFirst().getProduct().getName(),
                                    received, sold, waste, returned, maxId);
                        }
                ));
    }

    @Transactional
    public void saveDailyReport(Long storeId, LocalDate reportDate, BigDecimal totalRevenue, String note,
                                List<Long> productIds, List<BigDecimal> received,
                                List<BigDecimal> sold, List<BigDecimal> waste, List<BigDecimal> returns) {

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
                BigDecimal ret = (returns != null && i < returns.size()) ? returns.get(i) : null; // NOVO

                processMovement(product, store, r, MovementType.PURCHASE, report);
                processMovement(product, store, s, MovementType.SALE, report);
                processMovement(product, store, w, MovementType.WASTE, report);
                processMovement(product, store, ret, MovementType.RETURN, report); // NOVO
            }
        }
    }

    private void processMovement(Product p, Store s, BigDecimal qty, MovementType type, DailyStoreReport report) {
        if (qty != null && qty.compareTo(BigDecimal.ZERO) >= 0) {

            StockMovement existingMovement = movementRepository.findByReport(report).stream()
                    .filter(m -> m.getProduct().getId().equals(p.getId()) && m.getType() == type)
                    .findFirst()
                    .orElse(null);

            if (existingMovement != null) {
                BigDecimal oldQty = existingMovement.getQuantity();

                if (oldQty.compareTo(qty) != 0) {
                    BigDecimal diff = qty.subtract(oldQty);

                    existingMovement.setQuantity(qty);
                    movementRepository.save(existingMovement);

                    ProductStock stock = stockRepository.findByStoreAndProduct(s, p).orElse(new ProductStock());
                    if (stock.getId() == null) {
                        stock.setStore(s);
                        stock.setProduct(p);
                        stock.setQuantity(BigDecimal.ZERO);
                    }

                    if (type == MovementType.PURCHASE) {
                        stock.setQuantity(stock.getQuantity().add(diff));
                    } else {
                        // Prodaja, Otpis i POVRAT ovde oduzimaju zalihe
                        stock.setQuantity(stock.getQuantity().subtract(diff));
                    }
                    stockRepository.save(stock);
                }
            } else {
                StockMovement m = new StockMovement();
                m.setProduct(p);
                m.setStore(s);
                m.setQuantity(qty);
                m.setType(type);
                m.setReport(report);
                movementRepository.save(m);

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
                        // Oduzimanje za ostale tipove pa i za RETURN
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
                    String name = productMovements.getFirst().getProduct().getName();

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

                    // NOVO:
                    BigDecimal returned = productMovements.stream()
                            .filter(m -> m.getType() == MovementType.RETURN)
                            .map(StockMovement::getQuantity)
                            .reduce(BigDecimal::add).orElse(null);

                    return new MovementRowDTO(name, received, sold, waste, returned, maxId);
                })
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
                processMovement(product, store, BigDecimal.ZERO, MovementType.RETURN, report); // NOVO
            } else {
                if (currentStatus.getReceived() == null)
                    processMovement(product, store, BigDecimal.ZERO, MovementType.PURCHASE, report);
                if (currentStatus.getSold() == null)
                    processMovement(product, store, BigDecimal.ZERO, MovementType.SALE, report);
                if (currentStatus.getWaste() == null)
                    processMovement(product, store, BigDecimal.ZERO, MovementType.WASTE, report);
                if (currentStatus.getReturned() == null)
                    processMovement(product, store, BigDecimal.ZERO, MovementType.RETURN, report); // NOVO
            }
        }
    }
}