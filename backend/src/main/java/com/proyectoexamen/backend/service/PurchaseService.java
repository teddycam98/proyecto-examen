package com.proyectoexamen.backend.service;

import com.proyectoexamen.backend.dto.PurchaseForm;
import com.proyectoexamen.backend.entity.Purchase;
import java.util.List;

public interface PurchaseService {
    List<Purchase> findRecent();
    Purchase findById(Long id);
    Purchase create(PurchaseForm form);
    void cancel(Long id);
}
