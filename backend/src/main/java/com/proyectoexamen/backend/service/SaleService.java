package com.proyectoexamen.backend.service;

import com.proyectoexamen.backend.dto.SaleForm;
import com.proyectoexamen.backend.entity.Sale;
import java.util.List;

public interface SaleService {
    List<Sale> findRecent();
    Sale findById(Long id);
    Sale create(SaleForm form);
    void cancel(Long id);
}
