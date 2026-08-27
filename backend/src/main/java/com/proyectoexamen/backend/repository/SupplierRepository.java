package com.proyectoexamen.backend.repository;

import com.proyectoexamen.backend.entity.Supplier;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findAllByOrderByBusinessNameAsc();
    List<Supplier> findByActiveTrueOrderByBusinessNameAsc();
    boolean existsByDocumentNumberAndIdNot(String documentNumber, Long id);
    boolean existsByDocumentNumber(String documentNumber);
}
