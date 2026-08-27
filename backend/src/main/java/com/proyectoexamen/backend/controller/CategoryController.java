package com.proyectoexamen.backend.controller;

import com.proyectoexamen.backend.exception.BusinessException;
import com.proyectoexamen.backend.entity.Category;
import com.proyectoexamen.backend.service.CategoryService;
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
@RequestMapping("/categorias")
public class CategoryController {
    private final CategoryService service;
    public CategoryController(CategoryService service) { this.service = service; }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", service.findAll());
        return "categories/list";
    }

    @GetMapping("/nueva")
    public String form(Model model) {
        model.addAttribute("category", new Category());
        return "categories/form";
    }

    @GetMapping("/{id}/editar")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("category", service.findById(id));
        return "categories/form";
    }

    @PostMapping("/guardar")
    public String save(@Valid @ModelAttribute Category category, BindingResult result, Model model,
                       RedirectAttributes redirectAttributes) {
        if (!result.hasErrors()) {
            try {
                service.save(category);
                redirectAttributes.addFlashAttribute("success", "Categoria guardada correctamente");
                return "redirect:/categorias";
            } catch (BusinessException ex) { model.addAttribute("error", ex.getMessage()); }
        }
        return "categories/form";
    }

    @PostMapping("/{id}/estado")
    public String toggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.toggle(id);
        redirectAttributes.addFlashAttribute("success", "Estado de la categoria actualizado");
        return "redirect:/categorias";
    }
}
