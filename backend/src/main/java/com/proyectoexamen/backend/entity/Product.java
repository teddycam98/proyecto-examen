package com.proyectoexamen.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Entidad JPA que representa la tabla 'products' en la base de datos.
 * Almacena la información de catálogo, existencias, precios e imágenes de cada artículo.
 */
@Entity
@Table(name = "products", uniqueConstraints = @UniqueConstraint(name = "uk_product_code", columnNames = "code"))
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador único (Clave Primaria)

    @NotBlank(message = "El codigo es obligatorio")
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String code; // Código de producto (Ej. ART-COL-12)

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String name; // Nombre comercial del artículo

    @Size(max = 300)
    @Column(length = 300)
    private String description; // Descripción detallada

    @Size(max = 100)
    @Column(name = "image_name", length = 100)
    private String imageName; // Nombre del archivo de imagen asociado

    @NotNull(message = "Seleccione una unidad de medida")
    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measure", nullable = false, length = 20)
    private UnitOfMeasure unitOfMeasure = UnitOfMeasure.UNIDAD; // Enum Unidad de Medida

    @NotNull(message = "El precio de compra es obligatorio")
    @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
    @Column(name = "purchase_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal purchasePrice = BigDecimal.ZERO; // Precio costo de compra

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio de venta debe ser mayor que cero")
    @Column(name = "sale_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal salePrice = BigDecimal.ZERO; // Precio público de venta

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private int stock; // Cantidad física disponible en almacén

    @Min(value = 0, message = "El stock minimo no puede ser negativo")
    @Column(name = "minimum_stock", nullable = false)
    private int minimumStock; // Límite de alerta para reposición

    @NotNull(message = "Seleccione una categoria")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; // Relación con la categoría a la que pertenece

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier; // Relación opcional con el proveedor habitual

    @Column(nullable = false)
    private boolean active = true; // Estado (Habilitado/Deshabilitado)

    @Version
    @Column(nullable = false)
    private long version; // Control de concurrencia optimista

    // --- Métodos Getters y Setters con normalización de datos ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code == null ? null : code.trim().toUpperCase(); }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name == null ? null : name.trim(); }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description == null ? null : description.trim(); }
    
    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    
    public UnitOfMeasure getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }
    
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    
    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }
    
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    
    public int getMinimumStock() { return minimumStock; }
    public void setMinimumStock(int minimumStock) { this.minimumStock = minimumStock; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    
    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public long getVersion() { return version; }
    
    /**
     * Retorna verdadero si el stock actual es menor o igual al stock mínimo configurado.
     */
    public boolean isLowStock() { return stock <= minimumStock; }
}
