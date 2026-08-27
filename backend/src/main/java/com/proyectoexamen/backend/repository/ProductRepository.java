package com.proyectoexamen.backend.repository;

import com.proyectoexamen.backend.entity.Product;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"category", "supplier"})
    @Query("""
        select p from Product p
        where (:term = '' or lower(p.name) like lower(concat('%', :term, '%'))
               or lower(p.code) like lower(concat('%', :term, '%')))
        order by p.name
        """)
    List<Product> search(@Param("term") String term);

    @EntityGraph(attributePaths = {"category", "supplier"})
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findDetailedById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"category"})
    List<Product> findByActiveTrueOrderByNameAsc();

    @EntityGraph(attributePaths = {"category"})
    @Query("select p from Product p where p.active = true and p.stock <= p.minimumStock order by p.stock asc, p.name asc")
    List<Product> findLowStockProducts();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

    boolean existsByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);
    long countByActiveTrue();
}
