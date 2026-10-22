package com.proyectoexamen.backend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import com.proyectoexamen.backend.entity.*;
import com.proyectoexamen.backend.repository.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReportIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired ProductRepository products;
    @Autowired CategoryRepository categories;
    @Autowired SaleRepository sales;

    @Test
    void emptyReportsAndPageRender() throws Exception {
        mvc.perform(get("/reportes")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("Descargar inventario")));
        assertThat(text(download("/reportes/inventario.pdf"))).contains("No hay registros", "0.00");
        assertThat(text(download("/reportes/ventas.pdf?desde=2026-09-01&hasta=2026-09-30"))).contains("No hay registros");
    }

    @Test
    void salesIncludeEntireLastDayAndExcludeCancelledFromTotal() throws Exception {
        sale("PRIMERA", "2026-09-01T00:00:00", "10.00", TransactionStatus.COMPLETADA);
        sale("ULTIMA", "2026-09-30T23:59:59.999999", "20.00", TransactionStatus.COMPLETADA);
        sale("ANULADA", "2026-09-15T12:00:00", "99.00", TransactionStatus.ANULADA);
        sale("FUERA", "2026-10-01T00:00:00", "40.00", TransactionStatus.COMPLETADA);
        String pdf = text(download("/reportes/ventas.pdf?desde=2026-09-01&hasta=2026-09-30"));
        assertThat(pdf).contains("PRIMERA", "ULTIMA", "Anulada", "S/ 30.00").doesNotContain("FUERA", "S/ 129.00");
    }

    @Test
    void inventoryPaginatesAndExcludesInactiveProducts() throws Exception {
        Category category = new Category(); category.setName("Papelería y útiles escolares"); categories.save(category);
        for (int i = 0; i < 36; i++) {
            Product p = new Product(); p.setCode("REP-" + i);
            p.setName("Cuaderno de caligrafía y escritura " + i + " con cubierta reforzada para estudiantes de educación primaria");
            p.setCategory(category); p.setStock(2); p.setMinimumStock(3);
            p.setPurchasePrice(new BigDecimal("3.50")); p.setSalePrice(new BigDecimal("5.00"));
            p.setActive(i != 35); if (i == 35) p.setName("PRODUCTO INACTIVO"); products.save(p);
        }
        byte[] bytes = download("/reportes/inventario.pdf");
        PdfReader reader = new PdfReader(bytes);
        assertThat(reader.getNumberOfPages()).isGreaterThan(1); reader.close();
        assertThat(text(bytes)).contains("S/ 245.00", "REPOSICIÓN", "REP-34", "Página 2").doesNotContain("PRODUCTO INACTIVO");
        java.nio.file.Path output = java.nio.file.Path.of("target/report-qa/inventario-multipagina.pdf");
        java.nio.file.Files.createDirectories(output.getParent()); java.nio.file.Files.write(output, bytes);
    }

    @Test
    void invalidDatesReturnBadRequest() throws Exception {
        for (String query : new String[]{"desde=2026-09-30&hasta=2026-09-01", "desde=incorrecto&hasta=2026-09-01", "desde=2026-02-30&hasta=2026-09-01", "desde=2026-09-01"}) {
            mvc.perform(get("/reportes/ventas.pdf?" + query)).andExpect(status().isBadRequest());
        }
    }

    private byte[] download(String url) throws Exception {
        return mvc.perform(get(url)).andExpect(status().isOk()).andExpect(content().contentType("application/pdf"))
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.startsWith("attachment;")))
                .andReturn().getResponse().getContentAsByteArray();
    }

    private static String text(byte[] bytes) throws Exception {
        PdfReader reader = new PdfReader(bytes);
        try {
            StringBuilder text = new StringBuilder(); PdfTextExtractor extractor = new PdfTextExtractor(reader);
            for (int i = 1; i <= reader.getNumberOfPages(); i++) text.append(extractor.getTextFromPage(i));
            return text.toString();
        } finally { reader.close(); }
    }

    private void sale(String number, String date, String total, TransactionStatus status) {
        Sale sale = new Sale(); sale.setSaleNumber(number); sale.setCustomerName("María Peña");
        sale.setSaleDate(LocalDateTime.parse(date)); sale.setTotal(new BigDecimal(total)); sale.setStatus(status); sales.saveAndFlush(sale);
    }
}
