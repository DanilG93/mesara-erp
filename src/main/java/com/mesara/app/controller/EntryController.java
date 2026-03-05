package com.mesara.app.controller;

import com.mesara.app.domain.DailyStoreReport;
import com.mesara.app.domain.Product;
import com.mesara.app.domain.Store;
import com.mesara.app.dto.MovementRowDTO;
import com.mesara.app.service.EntryService;
import com.mesara.app.service.ProductService;
import com.mesara.app.service.StoreService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping({"/", "/entries"})
public class EntryController {

    private final EntryService entryService;
    private final StoreService storeService;
    private final ProductService productService;

    @GetMapping
    public String showEntryPage(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reportDate,
            @RequestParam(required = false, defaultValue = "false") boolean edit,
            Model model) {

        model.addAttribute("stores", storeService.getAllActive());
        LocalDate date = (reportDate != null) ? reportDate : LocalDate.now();
        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedStoreId", storeId);
        model.addAttribute("editMode", edit);

        if (storeId != null) {
            Store store = storeService.getById(storeId);
            DailyStoreReport report = entryService.findReportByStoreAndDate(store, date);
            model.addAttribute("report", report);

            if (report != null) {
                model.addAttribute("groupedMovements", entryService.getGroupedMovements(report));
                Map<Long, MovementRowDTO> movementMap = entryService.getMovementMap(report);
                model.addAttribute("movementMap", movementMap);

                List<Product> productsToShow;
                if (edit) {
                    productsToShow = productService.getAllActive();
                } else {
                    productsToShow = productService.getAllActive().stream()
                            .filter(p -> {
                                if (!movementMap.containsKey(p.getId())) return true;
                                MovementRowDTO status = movementMap.get(p.getId());
                                return status.getReceived() == null ||
                                        status.getSold() == null ||
                                        status.getWaste() == null ||
                                        status.getReturned() == null; // NOVO: Proveravamo i povrat
                            })
                            .toList();
                }
                model.addAttribute("products", productsToShow);
            } else {
                model.addAttribute("products", productService.getAllActive());
                model.addAttribute("movementMap", new java.util.HashMap<Long, MovementRowDTO>());
            }
        } else {
            model.addAttribute("products", productService.getAllActive());
            model.addAttribute("movementMap", new java.util.HashMap<Long, MovementRowDTO>());
        }

        return "entries";
    }

    @PostMapping("/save")
    public String save(
            @RequestParam Long storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reportDate,
            @RequestParam(required = false) BigDecimal totalRevenue,
            @RequestParam(required = false) String note,
            @RequestParam(required = false) List<Long> productIds,
            @RequestParam(required = false) List<BigDecimal> receivedAmounts,
            @RequestParam(required = false) List<BigDecimal> soldAmounts,
            @RequestParam(required = false) List<BigDecimal> wasteAmounts,
            @RequestParam(required = false) List<BigDecimal> returnAmounts) { // NOVO

        entryService.saveDailyReport(storeId, reportDate, totalRevenue, note,
                productIds, receivedAmounts, soldAmounts, wasteAmounts, returnAmounts); // Dodato slanje returnAmounts

        return "redirect:/entries?storeId=" + storeId + "&reportDate=" + reportDate + "&success=true";
    }

    @PostMapping("/finalize")
    public String finalizeDay(@RequestParam Long storeId,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reportDate) {

        entryService.finalizeDay(storeId, reportDate);

        return "redirect:/entries?storeId=" + storeId + "&reportDate=" + reportDate + "&finalized=true";
    }
}