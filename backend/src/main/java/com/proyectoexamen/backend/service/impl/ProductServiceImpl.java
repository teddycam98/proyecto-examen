package com.proyectoexamen.backend.service.impl;

import com.proyectoexamen.backend.exception.BusinessException;
import com.proyectoexamen.backend.exception.ResourceNotFoundException;
import com.proyectoexamen.backend.entity.Category;
import com.proyectoexamen.backend.entity.Product;
import com.proyectoexamen.backend.entity.Supplier;
import com.proyectoexamen.backend.repository.CategoryRepository;
import com.proyectoexamen.backend.repository.ProductRepository;
import com.proyectoexamen.backend.repository.SupplierRepository;
import com.proyectoexamen.backend.service.ProductImageService;
import com.proyectoexamen.backend.service.ProductService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementación de la Lógica de Negocio de Productos (Capa Service).
 * Contiene reglas de negocio como validación de códigos únicos, precios y persistencia transaccional.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final ProductImageService imageService;

    // Inyección de dependencias recomendada vía constructor
    public ProductServiceImpl(ProductRepository repository, CategoryRepository categoryRepository,
                           SupplierRepository supplierRepository, ProductImageService imageService) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.imageService = imageService;
    }

    /**
     * Busca productos por código o nombre de forma insensible a mayúsculas/minúsculas.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> search(String term) { 
        return repository.search(term == null ? "" : term.trim()); 
    }

    /**
     * Obtiene la lista de todos los productos en estado activo.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> findActive() { 
        return repository.findByActiveTrueOrderByNameAsc(); 
    }

    /**
     * Devuelve productos cuyo stock sea menor o igual a su stock mínimo (alertas).
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> findLowStock() { 
        return repository.findLowStockProducts(); 
    }

    /**
     * Busca un producto por ID con sus relaciones cargadas (categoría y proveedor).
     */
    @Override
    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return repository.findDetailedById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }

    /**
     * Guarda o actualiza un producto ejecutando validaciones de negocio.
     */
    @Override
    @Transactional
    public Product save(Product form, MultipartFile imageFile) {
        // Regla 1: Validar que el código del producto no esté duplicado
        boolean duplicate = form.getId() == null
            ? repository.existsByCodeIgnoreCase(form.getCode())
            : repository.existsByCodeIgnoreCaseAndIdNot(form.getCode(), form.getId());
        if (duplicate) {
            throw new BusinessException("Ya existe un producto con ese codigo");
        }

        // Regla 2: El precio de venta no puede ser inferior al precio de compra
        if (form.getSalePrice().compareTo(form.getPurchasePrice()) < 0) {
            throw new BusinessException("El precio de venta no puede ser menor que el precio de compra");
        }

        // Regla 3: Validar la existencia de la categoría seleccionada
        if (form.getCategory() == null || form.getCategory().getId() == null) {
            throw new BusinessException("Seleccione una categoria valida");
        }
        Category category = categoryRepository.findById(form.getCategory().getId())
            .orElseThrow(() -> new BusinessException("Seleccione una categoria valida"));

        // Regla 4: Validar proveedor si fue seleccionado
        Supplier supplier = null;
        if (form.getSupplier() != null && form.getSupplier().getId() != null) {
            supplier = supplierRepository.findById(form.getSupplier().getId())
                .orElseThrow(() -> new BusinessException("Seleccione un proveedor valido"));
        }

        // Mapeo o actualización de campos en la entidad administrada
        Product target = form.getId() == null ? new Product() : findById(form.getId());
        target.setCode(form.getCode());
        target.setName(form.getName());
        target.setDescription(form.getDescription());
        if (form.getImageName() != null) target.setImageName(form.getImageName());
        target.setUnitOfMeasure(form.getUnitOfMeasure());
        target.setPurchasePrice(form.getPurchasePrice());
        target.setSalePrice(form.getSalePrice());
        target.setMinimumStock(form.getMinimumStock());
        target.setCategory(category);
        target.setSupplier(supplier);
        target.setActive(form.isActive());

        // Almacenamiento seguro de la imagen enviada si aplica
        String imageName = imageService.store(imageFile);
        if (imageName != null) {
            target.setImageName(imageName);
        }

        return repository.save(target);
    }

    /**
     * Alterna el estado (Activo <-> Inactivo) utilizando un bloqueo pesimista.
     */
    @Override
    @Transactional
    public void toggle(Long id) {
        Product product = repository.findByIdForUpdate(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        product.setActive(!product.isActive());
    }

    /**
     * Cuenta la cantidad total de productos activos en el sistema.
     */
    @Override
    @Transactional(readOnly = true)
    public long countActive() { 
        return repository.countByActiveTrue(); 
    }
}
