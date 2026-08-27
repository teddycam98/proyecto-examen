package com.proyectoexamen.backend.service.impl;

import com.proyectoexamen.backend.dto.PurchaseForm;
import com.proyectoexamen.backend.dto.TransactionItemForm;
import com.proyectoexamen.backend.exception.BusinessException;
import com.proyectoexamen.backend.exception.ResourceNotFoundException;
import com.proyectoexamen.backend.entity.MovementType;
import com.proyectoexamen.backend.entity.Product;
import com.proyectoexamen.backend.entity.Purchase;
import com.proyectoexamen.backend.entity.PurchaseItem;
import com.proyectoexamen.backend.entity.Supplier;
import com.proyectoexamen.backend.entity.TransactionStatus;
import com.proyectoexamen.backend.repository.ProductRepository;
import com.proyectoexamen.backend.repository.PurchaseRepository;
import com.proyectoexamen.backend.repository.SupplierRepository;
import com.proyectoexamen.backend.service.PurchaseService;
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

@Service
public class PurchaseServiceImpl implements PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final StockService stockService;

    public PurchaseServiceImpl(PurchaseRepository purchaseRepository, ProductRepository productRepository,
                           SupplierRepository supplierRepository, StockService stockService) {
        this.purchaseRepository = purchaseRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.stockService = stockService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Purchase> findRecent() { return purchaseRepository.findTop20ByOrderByPurchaseDateDesc(); }

    @Override
    @Transactional(readOnly = true)
    public Purchase findById(Long id) {
        return purchaseRepository.findDetailedById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada"));
    }

    @Override
    @Transactional
    public Purchase create(PurchaseForm form) {
        Supplier supplier = supplierRepository.findById(form.getSupplierId())
            .orElseThrow(() -> new BusinessException("Seleccione un proveedor valido"));
        if (!supplier.isActive()) throw new BusinessException("El proveedor seleccionado esta inactivo");
        Map<Long, PurchaseLine> lines = normalize(form.getItems());
        if (lines.isEmpty()) throw new BusinessException("Agregue al menos un producto a la compra");

        Purchase purchase = new Purchase();
        purchase.setPurchaseNumber(generateNumber());
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setSupplier(supplier);
        purchase.setDocumentReference(blankToNull(form.getDocumentReference()));

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Long, PurchaseLine> entry : lines.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
            Product product = productRepository.findByIdForUpdate(entry.getKey())
                .orElseThrow(() -> new BusinessException("Uno de los productos ya no existe"));
            PurchaseLine line = entry.getValue();
            int previous = product.getStock();
            int updated = previous + line.quantity();
            product.setStock(updated);
            product.setPurchasePrice(line.unitCost());

            PurchaseItem item = new PurchaseItem();
            item.setProduct(product);
            item.setQuantity(line.quantity());
            item.setUnitCost(line.unitCost());
            item.setSubtotal(line.unitCost().multiply(BigDecimal.valueOf(line.quantity())));
            purchase.addItem(item);
            total = total.add(item.getSubtotal());
            stockService.record(product, MovementType.COMPRA, line.quantity(), previous, updated,
                purchase.getPurchaseNumber(), "Compra registrada");
        }
        purchase.setTotal(total);
        return purchaseRepository.save(purchase);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        Purchase purchase = findById(id);
        if (purchase.getStatus() == TransactionStatus.ANULADA) throw new BusinessException("La compra ya esta anulada");
        for (PurchaseItem item : purchase.getItems().stream().sorted((a, b) -> a.getProduct().getId().compareTo(b.getProduct().getId())).toList()) {
            Product product = productRepository.findByIdForUpdate(item.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
            if (product.getStock() < item.getQuantity()) {
                throw new BusinessException("No se puede anular: parte del stock de " + product.getName() + " ya fue utilizado");
            }
            int previous = product.getStock();
            int updated = previous - item.getQuantity();
            product.setStock(updated);
            stockService.record(product, MovementType.ANULACION_COMPRA, item.getQuantity(), previous, updated,
                purchase.getPurchaseNumber(), "Anulacion de compra");
        }
        purchase.setStatus(TransactionStatus.ANULADA);
    }

    private Map<Long, PurchaseLine> normalize(List<TransactionItemForm> items) {
        Map<Long, PurchaseLine> result = new LinkedHashMap<>();
        if (items == null) return result;
        for (TransactionItemForm item : items) {
            if (item.getProductId() == null) continue;
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new BusinessException("Todas las cantidades deben ser mayores que cero");
            }
            if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("Todos los costos deben ser mayores que cero");
            }
            PurchaseLine current = result.get(item.getProductId());
            if (current == null) result.put(item.getProductId(), new PurchaseLine(item.getQuantity(), item.getUnitPrice()));
            else if (current.unitCost().compareTo(item.getUnitPrice()) != 0) {
                throw new BusinessException("Un producto repetido debe tener el mismo costo");
            } else result.put(item.getProductId(), new PurchaseLine(current.quantity() + item.getQuantity(), current.unitCost()));
        }
        return result;
    }

    private String generateNumber() {
        return "C-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
            + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private record PurchaseLine(int quantity, BigDecimal unitCost) { }
}
