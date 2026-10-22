package com.proyectoexamen.backend.service.report;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.type.*;
import org.springframework.stereotype.Component;

/** Diseños A4 reutilizables. Se compilan una vez y se rellenan por solicitud. */
@Component
public class PdfReportRenderer {
    private static final Color NAVY = Color.decode("#102A43");
    private static final Color TEAL = Color.decode("#0F766E");
    private static final Color MUTED = Color.decode("#64748B");
    private final JasperReport inventory;
    private final JasperReport sales;

    public PdfReportRenderer() throws JRException {
        inventory = design("Inventario", new String[]{"CÓDIGO", "PRODUCTO / UNIDAD", "STOCK / MÍN.", "COSTO S/", "VALOR S/", "ALERTA"},
                new int[]{72, 163, 72, 65, 76, 75});
        sales = design("Ventas", new String[]{"COMPROBANTE", "FECHA", "CLIENTE", "ESTADO", "TOTAL S/"},
                new int[]{105, 87, 157, 90, 84});
    }

    public byte[] render(boolean isInventory, Map<String, Object> parameters, List<Map<String, ?>> rows) {
        try {
            JasperPrint print = JasperFillManager.fillReport(isInventory ? inventory : sales,
                    parameters, new JRMapCollectionDataSource(rows));
            return JasperExportManager.exportReportToPdf(print);
        } catch (JRException ex) {
            throw new IllegalStateException("No se pudo generar el reporte PDF", ex);
        }
    }

    private JasperReport design(String name, String[] headers, int[] widths) throws JRException {
        JasperDesign d = new JasperDesign();
        d.setName("Vico_" + name);
        d.setPageWidth(595); d.setPageHeight(842);
        d.setLeftMargin(36); d.setRightMargin(36); d.setTopMargin(30); d.setBottomMargin(30);
        d.setColumnWidth(523);
        d.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
        JRDesignStyle base = new JRDesignStyle();
        base.setName("Base"); base.setDefault(true); base.setFontName("DejaVu Sans");
        base.setFontSize(9f); base.setForecolor(NAVY); d.addStyle(base);
        for (String p : List.of("subtitle", "generated", "metric1", "metric2", "metric3", "label1", "label2", "label3", "note", "empty")) {
            JRDesignParameter parameter = new JRDesignParameter();
            parameter.setName(p); parameter.setValueClass(String.class); d.addParameter(parameter);
        }
        for (int i = 0; i < headers.length; i++) {
            JRDesignField field = new JRDesignField(); field.setName("c" + i); field.setValueClass(String.class); d.addField(field);
        }
        JRDesignBand title = band(211);
        box(title, 0, 0, 523, 87, NAVY);
        label(title, "LIBRERÍA VICO", 18, 13, 370, 19, 10, Color.WHITE, true);
        label(title, "Reporte de " + name.toLowerCase(java.util.Locale.ROOT), 18, 35, 480, 34, 25, Color.WHITE, true);
        box(title, 0, 87, 523, 4, TEAL);
        expr(title, "$P{subtitle}", 0, 103, 523, 18, 10, NAVY, false);
        for (int i = 0; i < 3; i++) {
            int x = i * 178;
            box(title, x, 134, 167, 62, Color.decode("#F0F6F7"));
            expr(title, "$P{label" + (i + 1) + "}", x + 10, 143, 147, 14, 8, MUTED, false);
            expr(title, "$P{metric" + (i + 1) + "}", x + 10, 161, 147, 26, 16, TEAL, true);
        }
        d.setTitle(title);
        JRDesignBand pageHeader = band(26);
        label(pageHeader, "VICO / CONTROL COMERCIAL", 0, 0, 250, 16, 8, TEAL, true);
        expr(pageHeader, "$P{generated}", 260, 0, 263, 16, 8, MUTED, false).setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
        d.setPageHeader(pageHeader);
        JRDesignBand columnHeader = band(29);
        box(columnHeader, 0, 0, 523, 26, NAVY);
        JRDesignBand detail = band(43);
        int x = 0;
        for (int i = 0; i < headers.length; i++) {
            label(columnHeader, headers[i], x + 4, 7, widths[i] - 8, 16, 7, Color.WHITE, true);
            JRDesignTextField cell = expr(detail, "$F{c" + i + "}", x + 4, 7, widths[i] - 8, 32, 8, NAVY, false);
            cell.setTextAdjust(TextAdjustEnum.STRETCH_HEIGHT);
            x += widths[i];
        }
        ((JRDesignSection) d.getDetailSection()).addBand(detail);
        d.setColumnHeader(columnHeader);
        JRDesignBand summary = band(78);
        expr(summary, "$P{empty}", 0, 5, 523, 22, 11, MUTED, false);
        box(summary, 0, 33, 3, 39, TEAL);
        expr(summary, "$P{note}", 13, 35, 510, 38, 9, MUTED, false).setTextAdjust(TextAdjustEnum.STRETCH_HEIGHT);
        d.setSummary(summary);
        JRDesignBand footer = band(30);
        box(footer, 0, 0, 523, 1, Color.decode("#DCE5EC"));
        label(footer, "Librería Vico  |  Reporte de gestión  |  PEN (S/)", 0, 10, 420, 16, 8, MUTED, false);
        expr(footer, "\"Página \" + $V{PAGE_NUMBER}", 430, 10, 93, 16, 8, MUTED, false).setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
        d.setPageFooter(footer);
        return JasperCompileManager.compileReport(d);
    }

    private static JRDesignBand band(int height) {
        JRDesignBand band = new JRDesignBand(); band.setHeight(height); band.setSplitType(SplitTypeEnum.STRETCH); return band;
    }

    private static void box(JRDesignBand band, int x, int y, int w, int h, Color color) {
        JRDesignRectangle r = new JRDesignRectangle(); r.setX(x); r.setY(y); r.setWidth(w); r.setHeight(h);
        r.setMode(ModeEnum.OPAQUE); r.setBackcolor(color); r.getLinePen().setLineWidth(0f); band.addElement(r);
    }

    private static void label(JRDesignBand band, String value, int x, int y, int w, int h, int size, Color color, boolean bold) {
        JRDesignStaticText t = new JRDesignStaticText(); t.setText(value);
        style(t, x, y, w, h, size, color, bold); band.addElement(t);
    }

    private static JRDesignTextField expr(JRDesignBand band, String expression, int x, int y, int w, int h, int size, Color color, boolean bold) {
        JRDesignTextField t = new JRDesignTextField(); t.setExpression(new JRDesignExpression(expression)); t.setBlankWhenNull(true);
        style(t, x, y, w, h, size, color, bold); band.addElement(t); return t;
    }

    private static void style(JRDesignTextElement t, int x, int y, int w, int h, int size, Color color, boolean bold) {
        t.setX(x); t.setY(y); t.setWidth(w); t.setHeight(h); t.setFontSize((float) size); t.setForecolor(color); t.setBold(bold);
    }
}
