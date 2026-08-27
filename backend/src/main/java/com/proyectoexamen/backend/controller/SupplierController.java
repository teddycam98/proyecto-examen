package com.proyectoexamen.backend.controller;

import com.proyectoexamen.backend.exception.BusinessException;
import com.proyectoexamen.backend.entity.Supplier;
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
@RequestMapping("/proveedores")
public class SupplierController {
    private final SupplierService service;
    public SupplierController(SupplierService service) { this.service = service; }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("suppliers", service.findAll());
        return "suppliers/list";
    }

    @GetMapping("/nuevo")
    public String form(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "suppliers/form";
    }

    @GetMapping("/{id}/editar")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("supplier", service.findById(id));
        return "suppliers/form";
    }

    @PostMapping("/guardar")
    public String save(@Valid @ModelAttribute Supplier supplier, BindingResult result, Model model,
                       RedirectAttributes redirectAttributes) {
        if (!result.hasErrors()) {
            try {
                service.save(supplier);
                redirectAttributes.addFlashAttribute("success", "Proveedor guardado correctamente");
                return "redirect:/proveedores";
            } catch (BusinessException ex) { model.addAttribute("error", ex.getMessage()); }
        }
        return "suppliers/form";
    }

    @PostMapping("/{id}/estado")
    public String toggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.toggle(id);
        redirectAttributes.addFlashAttribute("success", "Estado del proveedor actualizado");
        return "redirect:/proveedores";
    }
}
