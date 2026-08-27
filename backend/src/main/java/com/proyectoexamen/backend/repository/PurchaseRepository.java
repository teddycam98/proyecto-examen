package com.proyectoexamen.backend.repository;

import com.proyectoexamen.backend.entity.Purchase;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    @EntityGraph(attributePaths = {"supplier", "items", "items.product"})
    Optional<Purchase> findDetailedById(Long id);

    @EntityGraph(attributePaths = {"supplier"})
    List<Purchase> findTop20ByOrderByPurchaseDateDesc();
}
