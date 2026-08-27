package com.proyectoexamen.backend.service;

import java.math.BigDecimal;

public interface DashboardService {
    DashboardSummary summary();

    record DashboardSummary(long activeProducts, long lowStockProducts, long salesToday,
                            BigDecimal revenueToday) { }
}
