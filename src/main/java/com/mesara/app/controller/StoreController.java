package com.mesara.app.controller;

import com.mesara.app.domain.Store;
import com.mesara.app.service.StoreService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/stores")
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    public String showStoresPage(Model model) {
        model.addAttribute("stores", storeService.getAllStores());
        return "stores";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("store", new Store());
        return "store-form";
    }

    @PostMapping("/save")
    public String saveStore(@ModelAttribute("store") Store store) {
        storeService.saveStore(store);
        return "redirect:/stores";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Store store = storeService.getById(id);
        model.addAttribute("store", store);
        return "store-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteStore(@PathVariable Long id) {
        storeService.deleteStore(id);
        return "redirect:/stores";
    }


}
