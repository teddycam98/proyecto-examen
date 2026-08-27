INSERT INTO products (code, name, description, image_name, unit_of_measure, purchase_price, sale_price, stock, minimum_stock, category_id, supplier_id, active, version, created_at, updated_at) VALUES
    ('ART-COL-12', 'Colores Artesco 12 unid.', 'Caja de 12 colores escolares Artesco de minas suaves y resistentes', 'colores-artesco-12.jpg', 'CAJA', 8.50, 13.00, 40, 10, 5, 1, TRUE, 0, NOW(6), NOW(6)),
    ('STN-CUA-A4', 'Cuaderno Stanford A4 Cuadriculado', 'Cuaderno cosido A4 de 100 hojas cuadriculado Stanford con caratula decorada', 'cuaderno-stanford-a4.jpg', 'UNIDAD', 4.80, 7.20, 60, 15, 2, 1, TRUE, 0, NOW(6), NOW(6)),
    ('FAB-LAP-033', 'Pack Lapiceros Faber-Castell 033 (x3)', 'Set de 3 lapiceros de tinta fina Faber-Castell 033 (Azul, Rojo, Negro)', 'lapiceros-faber-033.jpg', 'PAQUETE', 2.20, 3.80, 100, 20, 1, 1, TRUE, 0, NOW(6), NOW(6)),
    ('ART-TEM-06', 'Tempera Artesco 6 Colores con Pincel', 'Estuche de 6 lavables de 15ml con pincel escolar incluido', 'temperas-artesco-6.jpg', 'JUEGO', 4.50, 7.00, 30, 8, 5, 1, TRUE, 0, NOW(6), NOW(6)),
    ('FAB-PLU-FIE', 'Plumones Faber-Castell Fiesta (x12)', 'Caja de 12 marcadores escolares lavables Faber-Castell Fiesta', 'plumones-faber-fiesta.jpg', 'CAJA', 9.00, 14.50, 35, 10, 1, 1, TRUE, 0, NOW(6), NOW(6)),
    ('ART-REG-30', 'Regla Transparente Artesco 30cm', 'Regla plastica transparente de 30 cm con graduacion en milimetros y pulgadas', 'regla-artesco-30cm.jpg', 'UNIDAD', 0.80, 1.50, 50, 12, 4, 1, TRUE, 0, NOW(6), NOW(6)),
    ('ART-GOM-21', 'Goma en Barra Artesco 21g', 'Barra adhesiva lavable de 21g ideal para papel y cartulina', 'goma-artesco-barra.jpg', 'UNIDAD', 2.50, 4.20, 45, 10, 4, 1, TRUE, 0, NOW(6), NOW(6)),
    ('ART-PLA-12', 'Plastilina Artesco 12 Barritas', 'Caja de plastilina escolar no toxica de 12 barritas multicolores', 'plastilina-artesco-12.jpg', 'CAJA', 3.20, 5.50, 50, 15, 5, 1, TRUE, 0, NOW(6), NOW(6));

INSERT INTO stock_movements (product_id, movement_type, quantity, previous_stock, new_stock, reference_number, notes, movement_date)
SELECT id, 'AJUSTE_ENTRADA', stock, 0, stock, 'INICIAL', 'Stock inicial de utiles escolares del Peru', NOW(6)
FROM products WHERE code IN ('ART-COL-12', 'STN-CUA-A4', 'FAB-LAP-033', 'ART-TEM-06', 'FAB-PLU-FIE', 'ART-REG-30', 'ART-GOM-21', 'ART-PLA-12');
