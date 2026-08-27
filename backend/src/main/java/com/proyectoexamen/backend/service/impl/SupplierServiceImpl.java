package com.proyectoexamen.backend.service.impl;

import com.proyectoexamen.backend.exception.BusinessException;
import com.proyectoexamen.backend.exception.ResourceNotFoundException;
import com.proyectoexamen.backend.entity.Supplier;
import com.proyectoexamen.backend.repository.SupplierRepository;
import com.proyectoexamen.backend.service.SupplierService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository repository;

    public SupplierServiceImpl(SupplierRepository repository) { this.repository = repository; }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> findAll() { return repository.findAllByOrderByBusinessNameAsc(); }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> findActive() { return repository.findByActiveTrueOrderByBusinessNameAsc(); }

    @Override
    @Transactional(readOnly = true)
    public Supplier findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));
    }

    @Override
    @Transactional
    public Supplier save(Supplier supplier) {
        String document = supplier.getDocumentNumber();
        if (document != null && !document.isBlank()) {
            boolean duplicate = supplier.getId() == null
                ? repository.existsByDocumentNumber(document)
                : repository.existsByDocumentNumberAndIdNot(document, supplier.getId());
            if (duplicate) throw new BusinessException("Ya existe un proveedor con ese documento");
        }
        return repository.save(supplier);
    }

    @Override
    @Transactional
    public void toggle(Long id) {
        Supplier supplier = findById(id);
        supplier.setActive(!supplier.isActive());
    }
}
