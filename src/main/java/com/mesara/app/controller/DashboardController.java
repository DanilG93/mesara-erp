package com.mesara.app.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping({"/", "/dashboard"})
public class DashboardController {

    @GetMapping
    public String showIndexPage() {
        return "index";
    }

}
