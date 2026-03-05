package com.mesara.app.service;

import com.mesara.app.domain.*;
import com.mesara.app.repository.DailyStoreReportRepository;
import com.mesara.app.repository.ProductStockRepository;
import com.mesara.app.repository.StockMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntryServiceTest {

    @Mock
    private DailyStoreReportRepository reportRepository;
    @Mock
    private StockMovementRepository movementRepository;
    @Mock
    private ProductStockRepository stockRepository;
    @Mock
    private StoreService storeService;
    @Mock
    private ProductService productService;

    @InjectMocks
    private EntryService entryService;

    @Captor
    private ArgumentCaptor<ProductStock> stockCaptor;
    @Captor
    private ArgumentCaptor<StockMovement> movementCaptor;

    private Store testStore;
    private Product testProduct;
    private DailyStoreReport testReport;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testStore = new Store();
        testStore.setId(1L);

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Svinjski but");

        testDate = LocalDate.now();

        testReport = new DailyStoreReport();
        testReport.setId(100L);
        testReport.setStore(testStore);
        testReport.setReportDate(testDate);
    }

    @Test
    void testFindReportByStoreAndDate() {
        when(reportRepository.findByStoreAndReportDate(testStore, testDate))
                .thenReturn(Optional.of(testReport));

        DailyStoreReport result = entryService.findReportByStoreAndDate(testStore, testDate);

        assertNotNull(result);
        assertEquals(100L, result.getId());
    }

    @Test
    void testSaveDailyReport_CreateNewMovements() {
        // GIVEN
        when(storeService.getById(1L)).thenReturn(testStore);
        when(productService.getById(1L)).thenReturn(testProduct);
        when(reportRepository.findByStoreAndReportDate(testStore, testDate)).thenReturn(Optional.of(testReport));
        when(reportRepository.save(any(DailyStoreReport.class))).thenReturn(testReport);

        when(movementRepository.findByReport(testReport)).thenReturn(List.of());
        when(stockRepository.findByStoreAndProduct(testStore, testProduct)).thenReturn(Optional.empty());

        // WHEN: Sada prosleđujemo i praznu listu za povrate (5. lista)
        entryService.saveDailyReport(
                1L, testDate, null, "Prvi unos",
                List.of(1L),
                List.of(BigDecimal.TEN), // Received
                List.of(),               // Sold
                List.of(),               // Waste
                List.of()                // Returns - NOVO!
        );

        // THEN: Pošto smo poslali samo Purchase (10), snima se 1 movement
        verify(movementRepository, times(1)).save(movementCaptor.capture());
        assertEquals(BigDecimal.TEN, movementCaptor.getValue().getQuantity());
        assertEquals(MovementType.PURCHASE, movementCaptor.getValue().getType());

        verify(stockRepository, times(1)).save(stockCaptor.capture());
        assertEquals(BigDecimal.TEN, stockCaptor.getValue().getQuantity());
    }

    @Test
    void testSaveDailyReport_UpdateExistingMovements() {
        // GIVEN
        when(storeService.getById(1L)).thenReturn(testStore);
        when(productService.getById(1L)).thenReturn(testProduct);
        when(reportRepository.findByStoreAndReportDate(testStore, testDate)).thenReturn(Optional.of(testReport));
        when(reportRepository.save(any(DailyStoreReport.class))).thenReturn(testReport);

        StockMovement existingMovement = new StockMovement();
        existingMovement.setProduct(testProduct);
        existingMovement.setType(MovementType.PURCHASE);
        existingMovement.setQuantity(BigDecimal.TEN);
        existingMovement.setReport(testReport);

        when(movementRepository.findByReport(testReport)).thenReturn(List.of(existingMovement));

        ProductStock currentStock = new ProductStock();
        currentStock.setId(1L);
        currentStock.setStore(testStore);
        currentStock.setProduct(testProduct);
        currentStock.setQuantity(BigDecimal.TEN);

        when(stockRepository.findByStoreAndProduct(testStore, testProduct)).thenReturn(Optional.of(currentStock));

        // WHEN: Sada prosleđujemo i listu za povrat (5. lista)
        entryService.saveDailyReport(
                1L, testDate, null, "Prepravka",
                List.of(1L),
                List.of(new BigDecimal("15")), // NOVO STANJE Nabavke
                List.of(),
                List.of(),
                List.of() // Returns - NOVO!
        );

        // THEN
        assertEquals(new BigDecimal("15"), existingMovement.getQuantity());
        verify(movementRepository, times(1)).save(existingMovement);

        verify(stockRepository, times(1)).save(stockCaptor.capture());
        assertEquals(new BigDecimal("15"), stockCaptor.getValue().getQuantity(), "Lager mora biti ažuriran sabiranjem samo razlike (diff)");
    }

    @Test
    void testFinalizeDay() {
        // GIVEN
        when(storeService.getById(1L)).thenReturn(testStore);
        when(reportRepository.findByStoreAndReportDate(testStore, testDate)).thenReturn(Optional.of(testReport));
        when(productService.getAllActive()).thenReturn(List.of(testProduct));

        when(movementRepository.findByReport(testReport)).thenReturn(List.of());

        // WHEN
        entryService.finalizeDay(1L, testDate);

        // THEN: Sada očekujemo 4 poziva jer imamo Nabavku, Prodaju, Otpis i POVRAT (sve po 0)
        verify(movementRepository, times(4)).save(movementCaptor.capture());
        List<StockMovement> savedMovements = movementCaptor.getAllValues();

        assertEquals(BigDecimal.ZERO, savedMovements.get(0).getQuantity());
        assertEquals(MovementType.PURCHASE, savedMovements.get(0).getType());

        // Provera da li se dodaje i Return (koji je četvrti u petlji)
        assertEquals(MovementType.RETURN, savedMovements.get(3).getType());
    }
}