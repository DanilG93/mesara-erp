package com.mesara.app.controller.web;

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
public class InventoryWebController {

    private final StockService stockService;
    private final StoreService storeService;

    @GetMapping
    public String showInventoryPage(@RequestParam(required = false) Long storeId, Model model) {
        List<Store> allStores = storeService.getAllStores();
        model.addAttribute("stores", allStores);

        Long resolvedStoreId = stockService.resolveStoreId(storeId);

        model.addAttribute("selectedStoreId", resolvedStoreId);

        if (resolvedStoreId != null) {
            model.addAttribute("stocks", stockService.getStockByStore(resolvedStoreId));
        } else {
            model.addAttribute("stocks", List.of());
        }

        return "inventory";
    }
}