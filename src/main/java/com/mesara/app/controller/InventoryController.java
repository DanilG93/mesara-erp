package com.mesara.app.controller;

import com.mesara.app.domain.Store;
import com.mesara.app.service.StockService;
import com.mesara.app.service.StoreService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/inventory")
public class InventoryController {

    private final StockService stockService;
    private final StoreService storeService;

    @GetMapping
    public String showInventoryPage(@RequestParam(required = false) Long storeId, Model model) {
        List<Store> allStores = storeService.getAllStores();
        model.addAttribute("stores", allStores);

        // LOGIKA ZA DIFOLTNU RADNJU:
        if (storeId == null) {
            storeId = stockService.getLatestStoreIdWithActivity();

            // Ako nema nikakvih aktivnosti u bazi, uzmi bar prvu radnju iz liste da ne bude prazno
            if (storeId == null && !allStores.isEmpty()) {
                storeId = allStores.getFirst().getId();
            }
        }

        model.addAttribute("selectedStoreId", storeId);
        model.addAttribute("stocks", stockService.getStockByStore(storeId));

        return "inventory";
    }
}