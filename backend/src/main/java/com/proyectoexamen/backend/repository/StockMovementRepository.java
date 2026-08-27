package com.proyectoexamen.backend.repository;

import com.proyectoexamen.backend.entity.StockMovement;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    @EntityGraph(attributePaths = {"product"})
    List<StockMovement> findTop100ByOrderByMovementDateDesc();
}
