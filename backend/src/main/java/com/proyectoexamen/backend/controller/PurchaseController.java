package com.proyectoexamen.backend.controller;

import com.proyectoexamen.backend.dto.PurchaseForm;
import com.proyectoexamen.backend.exception.BusinessException;
import com.proyectoexamen.backend.entity.Purchase;
import com.proyectoexamen.backend.service.ProductService;
import com.proyectoexamen.backend.service.PurchaseService;
import com.proyectoexamen.backend.service.SupplierService;
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
@RequestMapping("/compras")
public class PurchaseController {
    private final PurchaseService purchaseService;
    private final ProductService productService;
    private final SupplierService supplierService;

    public PurchaseController(PurchaseService purchaseService, ProductService productService,
                              SupplierService supplierService) {
        this.purchaseService = purchaseService;
        this.productService = productService;
        this.supplierService = supplierService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("purchases", purchaseService.findRecent());
        return "purchases/list";
    }

    @GetMapping("/nueva")
    public String form(Model model) {
        model.addAttribute("purchaseForm", new PurchaseForm());
        addCatalogs(model);
        return "purchases/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute PurchaseForm purchaseForm, BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {
        if (!result.hasErrors()) {
            try {
                Purchase purchase = purchaseService.create(purchaseForm);
                redirectAttributes.addFlashAttribute("success", "Compra registrada y stock actualizado");
                return "redirect:/compras/" + purchase.getId();
            } catch (BusinessException ex) { model.addAttribute("error", ex.getMessage()); }
        }
        addCatalogs(model);
        return "purchases/form";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("purchase", purchaseService.findById(id));
        return "purchases/detail";
    }

    @PostMapping("/{id}/anular")
    public String cancel(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            purchaseService.cancel(id);
            redirectAttributes.addFlashAttribute("success", "Compra anulada y stock actualizado");
        } catch (BusinessException ex) { redirectAttributes.addFlashAttribute("error", ex.getMessage()); }
        return "redirect:/compras/" + id;
    }

    private void addCatalogs(Model model) {
        model.addAttribute("products", productService.findActive());
        model.addAttribute("suppliers", supplierService.findActive());
    }
}
