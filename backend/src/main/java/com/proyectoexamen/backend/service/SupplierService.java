package com.proyectoexamen.backend.service;

import com.proyectoexamen.backend.entity.Supplier;
import java.util.List;

public interface SupplierService {
    List<Supplier> findAll();
    List<Supplier> findActive();
    Supplier findById(Long id);
    Supplier save(Supplier supplier);
    void toggle(Long id);
}
