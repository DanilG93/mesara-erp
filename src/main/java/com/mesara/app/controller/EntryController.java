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
@RequestMapping({"/","/entries"})
public class EntryController {

    private final EntryService entryService;
    private final StoreService storeService;
    private final ProductService productService;

    @GetMapping
    public String showEntryPage(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reportDate,
            @RequestParam(required = false, defaultValue = "false") boolean edit, // <--- DODATO
            Model model) {

        model.addAttribute("stores", storeService.getAllActive());
        LocalDate date = (reportDate != null) ? reportDate : LocalDate.now();
        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedStoreId", storeId);
        model.addAttribute("editMode", edit); // <--- DODATO: Šaljemo u HTML da zna u kom smo režimu

        if (storeId != null) {
            Store store = storeService.getById(storeId);
            DailyStoreReport report = entryService.findReportByStoreAndDate(store, date);
            model.addAttribute("report", report);

            if (report != null) {
                model.addAttribute("groupedMovements", entryService.getGroupedMovements(report));
                Map<Long, MovementRowDTO> movementMap = entryService.getMovementMap(report);
                model.addAttribute("movementMap", movementMap);

                // LOGIKA ZA DONJU TABELU:
                List<Product> remaining;
                if (edit) {
                    // AKO SMO U REŽIMU IZMENE: Svi artikli idu dole u input polja!
                    remaining = productService.getAllActive();
                } else {
                    // AKO NIJE IZMENA: Prikazujemo samo one koji nisu popunjeni (stara logika)
                    remaining = productService.getAllActive().stream()
                            .filter(p -> {
                                if (!movementMap.containsKey(p.getId())) return true;
                                MovementRowDTO status = movementMap.get(p.getId());
                                return status.getReceived() == null || status.getSold() == null || status.getWaste() == null;
                            }).toList();
                }
                model.addAttribute("products", remaining);
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
            @RequestParam(required = false) List<BigDecimal> wasteAmounts) {

        // Servis će sada znati da obradi situaciju i ako nema proizvoda (samo pazar)
        entryService.saveDailyReport(storeId, reportDate, totalRevenue, note,
                productIds, receivedAmounts, soldAmounts, wasteAmounts);

        return "redirect:/entries?storeId=" + storeId + "&reportDate=" + reportDate + "&success=true";
    }

    @PostMapping("/finalize")
    public String finalizeDay(@RequestParam Long storeId,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reportDate) {

        entryService.finalizeDay(storeId, reportDate);

        return "redirect:/entries?storeId=" + storeId + "&reportDate=" + reportDate + "&finalized=true";
    }
}