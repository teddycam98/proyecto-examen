package com.proyectoexamen.backend.service;

import com.proyectoexamen.backend.dto.StockAdjustmentForm;
import com.proyectoexamen.backend.entity.MovementType;
import com.proyectoexamen.backend.entity.Product;
import com.proyectoexamen.backend.entity.StockMovement;
import java.util.List;

public interface StockService {
    List<StockMovement> findRecent();
    void adjust(StockAdjustmentForm form);
    void record(Product product, MovementType type, int quantity, int previous, int updated,
                String reference, String notes);
}
