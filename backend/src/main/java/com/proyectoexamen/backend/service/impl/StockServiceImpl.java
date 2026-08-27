package com.proyectoexamen.backend.service.impl;

import com.proyectoexamen.backend.dto.StockAdjustmentForm;
import com.proyectoexamen.backend.exception.BusinessException;
import com.proyectoexamen.backend.exception.ResourceNotFoundException;
import com.proyectoexamen.backend.entity.MovementType;
import com.proyectoexamen.backend.entity.Product;
import com.proyectoexamen.backend.entity.StockMovement;
import com.proyectoexamen.backend.repository.ProductRepository;
import com.proyectoexamen.backend.repository.StockMovementRepository;
import com.proyectoexamen.backend.service.StockService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockServiceImpl implements StockService {
    private final ProductRepository productRepository;
    private final StockMovementRepository movementRepository;

    public StockServiceImpl(ProductRepository productRepository, StockMovementRepository movementRepository) {
        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovement> findRecent() { return movementRepository.findTop100ByOrderByMovementDateDesc(); }

    @Override
    @Transactional
    public void adjust(StockAdjustmentForm form) {
        if (form.getMovementType() != MovementType.AJUSTE_ENTRADA
                && form.getMovementType() != MovementType.AJUSTE_SALIDA) {
            throw new BusinessException("Tipo de ajuste no permitido");
        }
        Product product = productRepository.findByIdForUpdate(form.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        int previous = product.getStock();
        int quantity = form.getQuantity();
        int updated = form.getMovementType().isIncoming() ? previous + quantity : previous - quantity;
        if (updated < 0) throw new BusinessException("El ajuste dejaria el producto con stock negativo");
        product.setStock(updated);
        record(product, form.getMovementType(), quantity, previous, updated, "AJUSTE", form.getNotes());
    }

    public void record(Product product, MovementType type, int quantity, int previous, int updated,
                String reference, String notes) {
        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setMovementType(type);
        movement.setQuantity(quantity);
        movement.setPreviousStock(previous);
        movement.setNewStock(updated);
        movement.setReferenceNumber(reference);
        movement.setNotes(notes);
        movement.setMovementDate(LocalDateTime.now());
        movementRepository.save(movement);
    }
}
