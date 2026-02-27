package com.mesara.app.controller;

import com.mesara.app.dto.CategoryReportDTO;
import com.mesara.app.dto.MovementRowDTO;
import com.mesara.app.dto.StoreContributionDTO;
import com.mesara.app.service.PdfService;
import com.mesara.app.service.ProductService;
import com.mesara.app.service.ReportService;
import com.mesara.app.service.StoreService;
import com.mesara.app.utils.PdfUtils;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reports")
@AllArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final StoreService storeService;
    private final ProductService productService;
    private final PdfService pdfService;


    @GetMapping
    public String showAdvancedReports(
            @RequestParam(required = false) List<Long> storeIds,
            @RequestParam(required = false) List<Long> productIds,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model) {

        model.addAttribute("stores", storeService.getAllActive());
        model.addAttribute("allProducts", productService.getAllActive());

        LocalDate start = (from != null) ? from : LocalDate.now().withDayOfMonth(1);
        LocalDate end = (to != null) ? to : LocalDate.now();

        if (storeIds != null && !storeIds.isEmpty() && productIds != null && !productIds.isEmpty()) {
            // 1. Zbirni podaci (za tabelu i bedževe)
            List<MovementRowDTO> data = reportService.getAdvancedReport(storeIds, productIds, start, end);
            model.addAttribute("reportData", data);
            model.addAttribute("labels", data.stream().map(MovementRowDTO::getProductName).toList());
            model.addAttribute("topProducts", data.stream().limit(3).toList());

            List<CategoryReportDTO> catData = reportService.getCategoryTurnover(storeIds, productIds, start, end);
            model.addAttribute("categoryData", catData);

            // 2. NOVO: Detaljni podaci za "crtice" u grafikonu (po radnjama)
            var contributions = reportService.getStoreContributions(storeIds, productIds, start, end);
            model.addAttribute("storeContributions", contributions);

            // Treba nam lista imena radnji da bismo znali koliko "slojeva" crtamo
            List<String> uniqueStoreNames = contributions.stream()
                    .map(StoreContributionDTO::getStoreName)
                    .distinct().toList();
            model.addAttribute("uniqueStoreNames", uniqueStoreNames);
        }

        model.addAttribute("selectedStoreIds", storeIds);
        model.addAttribute("selectedProductIds", productIds);
        model.addAttribute("fromDate", start);
        model.addAttribute("toDate", end);

        return "reports";
    }

    @PostMapping("/pdf")
    public ResponseEntity<byte[]> exportToPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) List<Long> storeIds,
            @RequestParam(required = false) List<Long> productIds,
            @RequestParam("purchaseImg") String purchaseImg,
            @RequestParam("salesImg") String salesImg,
            @RequestParam("wasteImg") String wasteImg,
            @RequestParam("barChartImg") String barChartImg) {

        // 1. Sređivanje datuma (identično kao u GET metodi)
        LocalDate start = (from != null) ? from : LocalDate.now().withDayOfMonth(1);
        LocalDate end = (to != null) ? to : LocalDate.now();

        // 2. Priprema mape podataka za PDF Thymeleaf šablon
        Map<String, Object> data = new HashMap<>();
        data.put("fromDate", start);
        data.put("toDate", end);

        data.put("utils", new PdfUtils());

        // 3. Ubacujemo liste radnji i artikala (da bi se u PDF-u ispisala imena izabranih u zaglavlju)
        data.put("stores", storeService.getAllActive());
        data.put("allProducts", productService.getAllActive());
        data.put("selectedStoreIds", storeIds);
        data.put("selectedProductIds", productIds);

        // 4. Dohvatanje podataka za tabelu i Top artikle (samo ako su filteri izabrani)
        if (storeIds != null && !storeIds.isEmpty() && productIds != null && !productIds.isEmpty()) {
            List<MovementRowDTO> reportData = reportService.getAdvancedReport(storeIds, productIds, start, end);
            data.put("reportData", reportData);
            data.put("topProducts", reportData.stream().limit(3).toList());
        }

        // 5. Ubacivanje Base64 slika grafikona
        data.put("purchaseImg", purchaseImg);
        data.put("salesImg", salesImg);
        data.put("wasteImg", wasteImg);
        data.put("barChartImg", barChartImg);

        // 6. Generisanje PDF-a
        byte[] pdfBytes = pdfService.generatePdf("pdf-report", data);

        // 7. Vraćanje PDF fajla korisniku
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Izvestaj_Analitika.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }


}
