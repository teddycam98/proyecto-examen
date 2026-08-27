package com.proyectoexamen.backend.controller;

import com.proyectoexamen.backend.service.DashboardService;
import com.proyectoexamen.backend.service.ProductService;
import com.proyectoexamen.backend.service.SaleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    private final DashboardService dashboardService;
    private final ProductService productService;
    private final SaleService saleService;

    public DashboardController(DashboardService dashboardService, ProductService productService, SaleService saleService) {
        this.dashboardService = dashboardService;
        this.productService = productService;
        this.saleService = saleService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("summary", dashboardService.summary());
        model.addAttribute("lowStockProducts", productService.findLowStock().stream().limit(8).toList());
        model.addAttribute("recentSales", saleService.findRecent().stream().limit(6).toList());
        return "dashboard";
    }
}
