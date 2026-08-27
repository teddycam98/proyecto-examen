package com.proyectoexamen.backend.controller;

import com.proyectoexamen.backend.dto.StockAdjustmentForm;
import com.proyectoexamen.backend.exception.BusinessException;
import com.proyectoexamen.backend.entity.MovementType;
import com.proyectoexamen.backend.service.ProductService;
import com.proyectoexamen.backend.service.StockService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inventario")
public class StockController {
    private final StockService stockService;
    private final ProductService productService;

    public StockController(StockService stockService, ProductService productService) {
        this.stockService = stockService;
        this.productService = productService;
    }

    @GetMapping("/movimientos")
    public String movements(Model model) {
        model.addAttribute("movements", stockService.findRecent());
        return "stock/movements";
    }

    @GetMapping("/ajuste")
    public String adjustmentForm(Model model) {
        model.addAttribute("adjustmentForm", new StockAdjustmentForm());
        addFormData(model);
        return "stock/adjustment";
    }

    @PostMapping("/ajuste")
    public String adjust(@Valid @ModelAttribute StockAdjustmentForm adjustmentForm, BindingResult result,
                         Model model, RedirectAttributes redirectAttributes) {
        if (!result.hasErrors()) {
            try {
                stockService.adjust(adjustmentForm);
                redirectAttributes.addFlashAttribute("success", "Ajuste registrado correctamente");
                return "redirect:/inventario/movimientos";
            } catch (BusinessException ex) { model.addAttribute("error", ex.getMessage()); }
        }
        addFormData(model);
        return "stock/adjustment";
    }

    private void addFormData(Model model) {
        model.addAttribute("products", productService.findActive());
        model.addAttribute("adjustmentTypes", new MovementType[]{MovementType.AJUSTE_ENTRADA, MovementType.AJUSTE_SALIDA});
    }
}
