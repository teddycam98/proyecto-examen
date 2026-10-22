package com.proyectoexamen.backend.service;

import com.proyectoexamen.backend.entity.TransactionStatus;
import com.proyectoexamen.backend.repository.ProductRepository;
import com.proyectoexamen.backend.repository.SaleRepository;
import com.proyectoexamen.backend.service.report.PdfReportRenderer;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReportService {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final ProductRepository products;
    private final SaleRepository sales;
    private final PdfReportRenderer renderer;

    public ReportService(ProductRepository products, SaleRepository sales, PdfReportRenderer renderer) {
        this.products = products; this.sales = sales; this.renderer = renderer;
    }

    public byte[] inventory() {
        var data = products.findByActiveTrueOrderByNameAsc();
        List<Map<String, ?>> rows = new ArrayList<>();
        BigDecimal valuation = BigDecimal.ZERO;
        long lowStock = 0;
        for (var p : data) {
            BigDecimal value = p.getPurchasePrice().multiply(BigDecimal.valueOf(p.getStock()));
            valuation = valuation.add(value);
            if (p.isLowStock()) lowStock++;
            rows.add(row(p.getCode(), p.getName() + "\n" + p.getCategory().getName() + " / " + p.getUnitOfMeasure().getLabel(),
                    p.getStock() + " / " + p.getMinimumStock(), money(p.getPurchasePrice()), money(value),
                    p.isLowStock() ? "REPOSICIÓN" : "Normal"));
        }
        var params = parameters("Productos activos | Existencias al momento de emisión", rows.isEmpty());
        metrics(params, "PRODUCTOS ACTIVOS", "" + data.size(), "POR REPONER", "" + lowStock, "VALOR A COSTO", "S/ " + money(valuation));
        params.put("note", "Valorización = stock × precio de compra. Reposición: stock menor o igual al mínimo. Las cantidades se expresan en la unidad de cada producto.");
        return renderer.render(true, params, rows);
    }

    public byte[] sales(LocalDate from, LocalDate to) {
        if (from == null || to == null || from.isAfter(to) || to.equals(LocalDate.MAX)) {
            throw new IllegalArgumentException("Seleccione un período válido: desde debe ser anterior o igual a hasta.");
        }
        var data = sales.findForReport(from.atStartOfDay(), to.plusDays(1).atStartOfDay());
        List<Map<String, ?>> rows = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        long cancelled = 0;
        for (var s : data) {
            if (s.getStatus() == TransactionStatus.COMPLETADA) total = total.add(s.getTotal());
            else cancelled++;
            rows.add(row(s.getSaleNumber(), DATE.format(s.getSaleDate()), s.getCustomerName(), s.getStatus().getLabel(), money(s.getTotal())));
        }
        var params = parameters("Período: " + DATE.format(from) + " al " + DATE.format(to) + " | Todas las ventas del período", rows.isEmpty());
        metrics(params, "COMPROBANTES", "" + data.size(), "ANULADAS", "" + cancelled, "TOTAL COMPLETADAS", "S/ " + money(total));
        params.put("note", "El total incluye únicamente ventas completadas. Las ventas anuladas se muestran como referencia y no se suman. Importes expresados en soles.");
        return renderer.render(false, params, rows);
    }

    private static Map<String, Object> parameters(String subtitle, boolean empty) {
        Map<String, Object> p = new HashMap<>(); p.put("subtitle", subtitle);
        p.put("generated", "Emitido: " + ZonedDateTime.now(ZoneId.of("America/Lima")).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + " (Lima)");
        p.put("empty", empty ? "No hay registros para este reporte." : ""); return p;
    }

    private static void metrics(Map<String, Object> p, String l1, String v1, String l2, String v2, String l3, String v3) {
        p.put("label1", l1); p.put("metric1", v1); p.put("label2", l2); p.put("metric2", v2); p.put("label3", l3); p.put("metric3", v3);
    }

    private static Map<String, ?> row(String... values) {
        Map<String, Object> row = new HashMap<>();
        for (int i = 0; i < values.length; i++) row.put("c" + i, values[i]);
        return row;
    }

    private static String money(BigDecimal amount) {
        NumberFormat format = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-PE"));
        format.setMinimumFractionDigits(2); format.setMaximumFractionDigits(2); return format.format(amount);
    }
}
