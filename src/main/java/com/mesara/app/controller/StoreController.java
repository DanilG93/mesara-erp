package com.mesara.app.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/stores")
public class StoreController {

    @GetMapping
    public String showStoresPage() {

        return "stores";
    }


}
