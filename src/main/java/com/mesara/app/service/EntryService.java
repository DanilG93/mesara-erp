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

    // 1. Metoda koju koristi Kontroler za proveru postojećeg izveštaja
    public DailyStoreReport findReportByStoreAndDate(Store store, LocalDate date) {
        return reportRepository.findByStoreAndReportDate(store, date).orElse(null);
    }

    // 2. Metoda za izvlačenje već unetih artikala (za gornju tabelu)
    public List<StockMovement> getMovementsForReport(DailyStoreReport report) {
        return movementRepository.findByReport(report);
    }


    public Map<Long, MovementRowDTO> getMovementMap(DailyStoreReport report) {
        List<StockMovement> movements = movementRepository.findByReport(report);

        // Grupišemo po ID-ju proizvoda
        return movements.stream()
                .collect(Collectors.groupingBy(m -> m.getProduct().getId()))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            List<StockMovement> productMovements = entry.getValue();

                            // ISPRAVKA: Koristimo reduce bez početne nule da bi vratio null ako nema zapisa
                            BigDecimal received = productMovements.stream()
                                    .filter(m -> m.getType() == MovementType.PURCHASE)
                                    .map(StockMovement::getQuantity)
                                    .reduce(BigDecimal::add)
                                    .orElse(null);

                            BigDecimal sold = productMovements.stream()
                                    .filter(m -> m.getType() == MovementType.SALE)
                                    .map(StockMovement::getQuantity)
                                    .reduce(BigDecimal::add)
                                    .orElse(null);

                            BigDecimal waste = productMovements.stream()
                                    .filter(m -> m.getType() == MovementType.WASTE)
                                    .map(StockMovement::getQuantity)
                                    .reduce(BigDecimal::add)
                                    .orElse(null);

                            return new MovementRowDTO(productMovements.getFirst().getProduct().getName(), received, sold, waste);
                        }
                ));
    }


    // 3. Glavna metoda za snimanje svega
    @Transactional
    public void saveDailyReport(Long storeId, LocalDate reportDate, BigDecimal totalRevenue, String note,
                                List<Long> productIds, List<BigDecimal> received,
                                List<BigDecimal> sold, List<BigDecimal> waste) {

        Store store = storeService.getById(storeId);
        DailyStoreReport report = reportRepository.findByStoreAndReportDate(store, reportDate)
                .orElse(new DailyStoreReport());

        // Ako izveštaj ne postoji, postavljamo osnovne podatke
        if (report.getId() == null) {
            report.setStore(store);
            report.setReportDate(reportDate);
        }

        // PAZAR: Snimamo ga samo ako je poslat i ako već nije bio unet (ili ga ažuriramo ako je bio null)
        if (totalRevenue != null) {
            report.setTotalRevenue(totalRevenue);
        }

        if (note != null && !note.isEmpty()) {
            report.setNote(note);
        }

        report = reportRepository.save(report);

        // ARTIKLI: Procesuiramo listu samo ako je stigla (nije null)
        if (productIds != null) {
            for (int i = 0; i < productIds.size(); i++) {
                Product product = productService.getById(productIds.get(i));

                // Provera da li liste imaju podatke na tom indeksu
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
        // Snimamo samo ako je količina uneta i veća od 0
        if (qty != null && qty.compareTo(BigDecimal.ZERO) >= 0) {

            // A. Snimanje u ISTORIJU
            StockMovement m = new StockMovement();
            m.setProduct(p);
            m.setStore(s);
            m.setQuantity(qty);
            m.setType(type);
            m.setReport(report);
            movementRepository.save(m);

            // B. Ažuriranje TRENUTNOG LAGERA (ProductStock)
            if (qty.compareTo(BigDecimal.ZERO) > 0) {
                ProductStock stock = stockRepository.findByStoreAndProduct(s, p)
                        .orElse(new ProductStock());

                if (stock.getId() == null) {
                    stock.setStore(s);
                    stock.setProduct(p);
                    stock.setQuantity(BigDecimal.ZERO);
                }

                // Nabavka dodaje na lager, ostalo oduzima
                if (type == MovementType.PURCHASE) {
                    stock.setQuantity(stock.getQuantity().add(qty));
                } else {
                    stock.setQuantity(stock.getQuantity().subtract(qty));
                }
                stockRepository.save(stock);
            }
        }
    }

    public List<MovementRowDTO> getGroupedMovements(DailyStoreReport report) {
        List<StockMovement> movements = movementRepository.findByReport(report);

        return movements.stream()
                .collect(Collectors.groupingBy(m -> m.getProduct().getName()))
                .entrySet().stream()
                .map(entry -> {
                    String name = entry.getKey();
                    List<StockMovement> productMovements = entry.getValue();

                    // NABAVKA: orElse(null) omogućava razlikovanje unosa od praznog polja
                    BigDecimal received = productMovements.stream()
                            .filter(m -> m.getType() == MovementType.PURCHASE)
                            .map(StockMovement::getQuantity)
                            .reduce(BigDecimal::add)
                            .orElse(null);

                    // PRODATO
                    BigDecimal sold = productMovements.stream()
                            .filter(m -> m.getType() == MovementType.SALE)
                            .map(StockMovement::getQuantity)
                            .reduce(BigDecimal::add)
                            .orElse(null);

                    // OTPIS
                    BigDecimal waste = productMovements.stream()
                            .filter(m -> m.getType() == MovementType.WASTE)
                            .map(StockMovement::getQuantity)
                            .reduce(BigDecimal::add)
                            .orElse(null);

                    return new MovementRowDTO(name, received, sold, waste);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void finalizeDay(Long storeId, LocalDate reportDate) {
        Store store = storeService.getById(storeId);
        DailyStoreReport report = reportRepository.findByStoreAndReportDate(store, reportDate)
                .orElseThrow(() -> new RuntimeException("Izvještaj mora postojati da bi se završio dan."));

        // 1. Uzimamo mapu trenutnog stanja (šta je već uneseno)
        Map<Long, MovementRowDTO> movementMap = getMovementMap(report);

        // 2. Uzimamo sve aktivne proizvode
        List<Product> activeProducts = productService.getAllActive();

        for (Product product : activeProducts) {
            MovementRowDTO currentStatus = movementMap.get(product.getId());

            // Ako artikal uopšte nije u mapi ili mu fali neka kolona, setujemo 0
            if (currentStatus == null) {
                processMovement(product, store, BigDecimal.ZERO, MovementType.PURCHASE, report);
                processMovement(product, store, BigDecimal.ZERO, MovementType.SALE, report);
                processMovement(product, store, BigDecimal.ZERO, MovementType.WASTE, report);
            } else {
                if (currentStatus.getReceived() == null) {
                    processMovement(product, store, BigDecimal.ZERO, MovementType.PURCHASE, report);
                }
                if (currentStatus.getSold() == null) {
                    processMovement(product, store, BigDecimal.ZERO, MovementType.SALE, report);
                }
                if (currentStatus.getWaste() == null) {
                    processMovement(product, store, BigDecimal.ZERO, MovementType.WASTE, report);
                }
            }
        }
    }
}