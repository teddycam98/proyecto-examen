package com.proyectoexamen.backend.controller;

import com.proyectoexamen.backend.service.ReportService;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reportes")
public class ReportController {
    private final ReportService reports;
    public ReportController(ReportService reports) { this.reports = reports; }

    @GetMapping
    public String index(Model model) {
        LocalDate today = LocalDate.now(ZoneId.of("America/Lima"));
        model.addAttribute("from", today.withDayOfMonth(1)); model.addAttribute("to", today);
        return "reports/index";
    }

    @GetMapping("/inventario.pdf")
    public ResponseEntity<byte[]> inventory() { return pdf(reports.inventory(), "inventario-vico.pdf"); }

    @GetMapping("/ventas.pdf")
    public ResponseEntity<byte[]> sales(@RequestParam String desde, @RequestParam String hasta) {
        return pdf(reports.sales(LocalDate.parse(desde), LocalDate.parse(hasta)), "ventas-vico.pdf");
    }

    @ExceptionHandler({IllegalArgumentException.class, java.time.DateTimeException.class,
            org.springframework.web.bind.MissingServletRequestParameterException.class})
    public ResponseEntity<String> invalidPeriod(Exception ex) {
        return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN)
                .body("Indique fechas válidas en formato AAAA-MM-DD; desde debe ser anterior o igual a hasta.");
    }

    private static ResponseEntity<byte[]> pdf(byte[] body, String filename) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(filename).build().toString())
                .cacheControl(CacheControl.noStore()).contentLength(body.length).body(body);
    }
}
