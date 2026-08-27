package com.proyectoexamen.backend.controller;

import com.proyectoexamen.backend.dto.SaleForm;
import com.proyectoexamen.backend.exception.BusinessException;
import com.proyectoexamen.backend.entity.Sale;
import com.proyectoexamen.backend.service.ProductService;
import com.proyectoexamen.backend.service.CategoryService;
import com.proyectoexamen.backend.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ventas")
public class SaleController {
    private final SaleService saleService;
    private final ProductService productService;
    private final CategoryService categoryService;

    public SaleController(SaleService saleService, ProductService productService, CategoryService categoryService) {
        this.saleService = saleService;
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("sales", saleService.findRecent());
        return "sales/list";
    }

    @GetMapping("/nueva")
    public String form(Model model) {
        model.addAttribute("saleForm", new SaleForm());
        addProducts(model);
        return "sales/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute SaleForm saleForm, BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {
        if (!result.hasErrors()) {
            try {
                Sale sale = saleService.create(saleForm);
                redirectAttributes.addFlashAttribute("success", "Venta registrada y stock actualizado");
                return "redirect:/ventas/" + sale.getId();
            } catch (BusinessException ex) { model.addAttribute("error", ex.getMessage()); }
        }
        addProducts(model);
        return "sales/form";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("sale", saleService.findById(id));
        return "sales/detail";
    }

    @PostMapping("/{id}/anular")
    public String cancel(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            saleService.cancel(id);
            redirectAttributes.addFlashAttribute("success", "Venta anulada y stock restituido");
        } catch (BusinessException ex) { redirectAttributes.addFlashAttribute("error", ex.getMessage()); }
        return "redirect:/ventas/" + id;
    }

    private void addProducts(Model model) {
        model.addAttribute("products", productService.findActive());
        model.addAttribute("categories", categoryService.findActive());
    }
}
