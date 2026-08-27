package com.proyectoexamen.backend.service.impl;

import com.proyectoexamen.backend.dto.SaleForm;
import com.proyectoexamen.backend.dto.TransactionItemForm;
import com.proyectoexamen.backend.exception.BusinessException;
import com.proyectoexamen.backend.exception.ResourceNotFoundException;
import com.proyectoexamen.backend.entity.MovementType;
import com.proyectoexamen.backend.entity.Product;
import com.proyectoexamen.backend.entity.Sale;
import com.proyectoexamen.backend.entity.SaleItem;
import com.proyectoexamen.backend.entity.TransactionStatus;
import com.proyectoexamen.backend.repository.ProductRepository;
import com.proyectoexamen.backend.repository.SaleRepository;
import com.proyectoexamen.backend.service.SaleService;
import com.proyectoexamen.backend.service.StockService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de la Lógica de Negocio de Ventas y Punto de Venta.
 * Maneja la verificación estricta de existencias, cálculo transaccional de montos
 * y la reversa del stock en caso de anulación de comprobantes.
 */
@Service
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final StockService stockService;

    public SaleServiceImpl(SaleRepository saleRepository, ProductRepository productRepository, StockService stockService) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.stockService = stockService;
    }

    /**
     * Obtiene las 20 ventas más recientes ordenadas por fecha descendente.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Sale> findRecent() { 
        return saleRepository.findTop20ByOrderBySaleDateDesc(); 
    }

    /**
     * Obtiene el detalle completo de una venta por su ID.
     */
    @Override
    @Transactional(readOnly = true)
    public Sale findById(Long id) {
        return saleRepository.findDetailedById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));
    }

    /**
     * Procesa y registra una nueva venta en el sistema.
     * Disminuye el stock de los productos con bloqueo pesimista y genera los movimientos de kardex.
     */
    @Override
    @Transactional
    public Sale create(SaleForm form) {
        // Agrupa y valida las cantidades seleccionadas en el carrito
        Map<Long, Integer> quantities = normalize(form.getItems());
        if (quantities.isEmpty()) {
            throw new BusinessException("Agregue al menos un producto a la venta");
        }

        Sale sale = new Sale();
        sale.setSaleNumber(generateNumber("V"));
        sale.setSaleDate(LocalDateTime.now());
        String customer = form.getCustomerName() == null ? "" : form.getCustomerName().trim();
        sale.setCustomerName(customer.isBlank() ? "Publico general" : customer);
        sale.setCustomerDocument(blankToNull(form.getCustomerDocument()));

        BigDecimal total = BigDecimal.ZERO;
        
        // Se procesan los ítems ordenados por ID de producto para prevenir Deadlocks en la BD
        for (Map.Entry<Long, Integer> entry : quantities.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
            // Bloqueo SELECT FOR UPDATE para evitar ventas concurrentes sin stock
            Product product = productRepository.findByIdForUpdate(entry.getKey())
                .orElseThrow(() -> new BusinessException("Uno de los productos ya no existe"));
            
            if (!product.isActive()) {
                throw new BusinessException(product.getName() + " esta inactivo");
            }
            
            int quantity = entry.getValue();
            if (product.getStock() < quantity) {
                throw new BusinessException("Stock insuficiente para " + product.getName() + ". Disponible: " + product.getStock());
            }

            // Descuento de stock
            int previous = product.getStock();
            int updated = previous - quantity;
            product.setStock(updated);

            // Detalle de la línea de venta
            SaleItem item = new SaleItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setUnitPrice(product.getSalePrice());
            item.setSubtotal(product.getSalePrice().multiply(BigDecimal.valueOf(quantity)));
            sale.addItem(item);

            total = total.add(item.getSubtotal());

            // Registro inmutable en el Kardex (StockMovements)
            stockService.record(product, MovementType.VENTA, quantity, previous, updated, sale.getSaleNumber(), "Venta registrada");
        }

        sale.setTotal(total);
        return saleRepository.save(sale);
    }

    /**
     * Anula una venta y devuelve las cantidades al stock de origen.
     */
    @Override
    @Transactional
    public void cancel(Long id) {
        Sale sale = findById(id);
        if (sale.getStatus() == TransactionStatus.ANULADA) {
            throw new BusinessException("La venta ya esta anulada");
        }

        // Restitución de existencias por cada ítem
        for (SaleItem item : sale.getItems().stream().sorted((a, b) -> a.getProduct().getId().compareTo(b.getProduct().getId())).toList()) {
            Product product = productRepository.findByIdForUpdate(item.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
            
            int previous = product.getStock();
            int updated = previous + item.getQuantity();
            product.setStock(updated);

            // Kardex: Anulación de venta
            stockService.record(product, MovementType.ANULACION_VENTA, item.getQuantity(), previous, updated,
                sale.getSaleNumber(), "Anulacion de venta");
        }

        sale.setStatus(TransactionStatus.ANULADA);
    }

    /**
     * Limpia y consolida la lista de productos enviados por el formulario web.
     */
    private Map<Long, Integer> normalize(List<TransactionItemForm> items) {
        Map<Long, Integer> result = new LinkedHashMap<>();
        if (items == null) return result;
        for (TransactionItemForm item : items) {
            if (item.getProductId() == null) continue;
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new BusinessException("Todas las cantidades deben ser mayores que cero");
            }
            result.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }
        return result;
    }

    /**
     * Genera un número secuencial/único de comprobante de venta (Ej. V-202608271200-A1B2).
     */
    private String generateNumber(String prefix) {
        return prefix + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
            + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
