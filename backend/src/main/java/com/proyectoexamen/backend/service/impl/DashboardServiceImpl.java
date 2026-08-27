package com.proyectoexamen.backend.service.impl;

import com.proyectoexamen.backend.entity.TransactionStatus;
import com.proyectoexamen.backend.repository.SaleRepository;
import com.proyectoexamen.backend.service.DashboardService;
import com.proyectoexamen.backend.service.ProductService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final ProductService productService;
    private final SaleRepository saleRepository;

    public DashboardServiceImpl(ProductService productService, SaleRepository saleRepository) {
        this.productService = productService;
        this.saleRepository = saleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummary summary() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay().minusNanos(1);
        return new DashboardSummary(
            productService.countActive(),
            productService.findLowStock().size(),
            saleRepository.countBySaleDateBetweenAndStatus(start, end, TransactionStatus.COMPLETADA),
            saleRepository.sumTotalBetween(start, end, TransactionStatus.COMPLETADA)
        );
    }
}
