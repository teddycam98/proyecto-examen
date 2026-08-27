package com.proyectoexamen.backend.repository;

import com.proyectoexamen.backend.entity.Sale;
import com.proyectoexamen.backend.entity.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<Sale> findDetailedById(Long id);

    List<Sale> findTop20ByOrderBySaleDateDesc();

    long countBySaleDateBetweenAndStatus(LocalDateTime start, LocalDateTime end, TransactionStatus status);

    @Query("select coalesce(sum(s.total), 0) from Sale s where s.saleDate between :start and :end and s.status = :status")
    BigDecimal sumTotalBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                               @Param("status") TransactionStatus status);
}
