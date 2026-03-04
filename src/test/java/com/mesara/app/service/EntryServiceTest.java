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


        entryService.saveDailyReport(
                1L, testDate, null, "Prvi unos",
                List.of(1L),
                List.of(BigDecimal.TEN),
                List.of(),
                List.of()
        );


        verify(movementRepository, times(1)).save(movementCaptor.capture());
        assertEquals(BigDecimal.TEN, movementCaptor.getValue().getQuantity());
        assertEquals(MovementType.PURCHASE, movementCaptor.getValue().getType());


        verify(stockRepository, times(1)).save(stockCaptor.capture());
        assertEquals(BigDecimal.TEN, stockCaptor.getValue().getQuantity());
    }


    @Test
    void testSaveDailyReport_UpdateExistingMovements() {

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

        entryService.saveDailyReport(
                1L, testDate, null, "Prepravka",
                List.of(1L),
                List.of(new BigDecimal("15")), // NOVO STANJE
                List.of(), List.of()
        );


        assertEquals(new BigDecimal("15"), existingMovement.getQuantity());
        verify(movementRepository, times(1)).save(existingMovement);

        verify(stockRepository, times(1)).save(stockCaptor.capture());
        assertEquals(new BigDecimal("15"), stockCaptor.getValue().getQuantity(), "Lager mora biti ažuriran sabiranjem samo razlike (diff)");
    }

    @Test
    void testFinalizeDay() {

        when(storeService.getById(1L)).thenReturn(testStore);
        when(reportRepository.findByStoreAndReportDate(testStore, testDate)).thenReturn(Optional.of(testReport));
        when(productService.getAllActive()).thenReturn(List.of(testProduct));


        when(movementRepository.findByReport(testReport)).thenReturn(List.of());


        entryService.finalizeDay(1L, testDate);

        verify(movementRepository, times(3)).save(movementCaptor.capture());
        List<StockMovement> savedMovements = movementCaptor.getAllValues();

        assertEquals(BigDecimal.ZERO, savedMovements.getFirst().getQuantity());
        assertEquals(MovementType.PURCHASE, savedMovements.getFirst().getType());
    }
}