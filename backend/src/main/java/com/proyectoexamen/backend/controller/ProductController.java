package com.proyectoexamen.backend.controller;

import com.proyectoexamen.backend.exception.BusinessException;
import com.proyectoexamen.backend.entity.Category;
import com.proyectoexamen.backend.entity.Product;
import com.proyectoexamen.backend.entity.Supplier;
import com.proyectoexamen.backend.entity.UnitOfMeasure;
import com.proyectoexamen.backend.service.CategoryService;
import com.proyectoexamen.backend.service.ProductService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador de Productos (Capa de Presentación Web).
 * Maneja las peticiones HTTP relativas a la gestión del catálogo de productos.
 */
@Controller
@RequestMapping("/productos")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;

    // Inyección de dependencias por constructor
    public ProductController(ProductService productService, CategoryService categoryService,
                             SupplierService supplierService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.supplierService = supplierService;
    }

    /**
     * Muestra el listado de productos con soporte para búsqueda por término.
     * GET /productos?q=palabra
     */
    @GetMapping
    public String list(@RequestParam(defaultValue = "") String q, Model model) {
        model.addAttribute("products", productService.search(q));
        model.addAttribute("q", q);
        return "products/list";
    }

    /**
     * Muestra el formulario para registrar un nuevo producto.
     * GET /productos/nuevo
     */
    @GetMapping("/nuevo")
    public String createForm(Model model) {
        Product product = new Product();
        product.setCategory(new Category());
        product.setSupplier(new Supplier());
        model.addAttribute("product", product);
        addCatalogs(model); // Carga combos de categorías, proveedores y unidades
        return "products/form";
    }

    /**
     * Muestra el formulario para editar un producto existente por su ID.
     * GET /productos/{id}/editar
     */
    @GetMapping("/{id}/editar")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        addCatalogs(model);
        return "products/form";
    }

    /**
     * Procesa la creación o actualización de un producto junto con su imagen.
     * POST /productos/guardar
     */
    @PostMapping("/guardar")
    public String save(@Valid @ModelAttribute Product product, BindingResult result, Model model,
                       @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
                       RedirectAttributes redirectAttributes) {
        // Validación de anotaciones @NotNull, @Min, etc. en la entidad
        if (result.hasErrors()) {
            addCatalogs(model);
            return "products/form";
        }
        try {
            Product saved = productService.save(product, imageFile);
            redirectAttributes.addFlashAttribute("success", "Producto guardado correctamente");
            return "redirect:/productos/" + saved.getId() + "/editar";
        } catch (BusinessException ex) {
            // Captura errores de negocio (ej. código duplicado o precio inválido)
            model.addAttribute("error", ex.getMessage());
            addCatalogs(model);
            return "products/form";
        }
    }

    /**
     * Cambia el estado (Activo / Inactivo) de un producto.
     * POST /productos/{id}/estado
     */
    @PostMapping("/{id}/estado")
    public String toggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.toggle(id);
        redirectAttributes.addFlashAttribute("success", "Estado del producto actualizado");
        return "redirect:/productos";
    }

    /**
     * Método auxiliar para cargar catálogos requeridos por el formulario.
     */
    private void addCatalogs(Model model) {
        model.addAttribute("categories", categoryService.findActive());
        model.addAttribute("suppliers", supplierService.findActive());
        model.addAttribute("units", UnitOfMeasure.values());
    }
}
