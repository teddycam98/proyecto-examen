INSERT INTO products (code, name, description, image_name, unit_of_measure, purchase_price, sale_price, stock, minimum_stock, category_id, supplier_id, active, version, created_at, updated_at) VALUES
    ('FAB-COL-24', 'Colores Faber-Castell 24 unid.', 'Caja de 24 colores clasicos Faber-Castell escolares', 'colores-faber-24.jpg', 'CAJA', 16.50, 24.00, 30, 8, 5, 1, TRUE, 0, NOW(6), NOW(6)),
    ('MCH-ESC-PER', 'Mochila Escolar Ergonomica', 'Mochila escolar resistente reforzada con tirantes acolchados', 'mochila-escolar.jpg', 'UNIDAD', 35.00, 55.00, 20, 5, 3, 1, TRUE, 0, NOW(6), NOW(6)),
    ('FAB-TAJ-DEP', 'Tajador Faber-Castell con Deposito', 'Tajador doble orificio con deposito para residuos', 'tajador-deposito.jpg', 'UNIDAD', 2.80, 4.50, 60, 15, 4, 1, TRUE, 0, NOW(6), NOW(6)),
    ('FAB-BOR-BLA', 'Borrador Blanco Faber-Castell', 'Borrador sintético blanco libre de PVC', 'borrador-faber.jpg', 'UNIDAD', 1.00, 1.80, 80, 20, 1, 1, TRUE, 0, NOW(6), NOW(6)),
    ('VIN-CAR-PAC', 'Pack Cartulinas y Papel Lustre Vinifan', 'Paquete de cartulinas y papeles escolares surtidos', 'cartulina-pack.jpg', 'PAQUETE', 6.50, 10.50, 40, 10, 2, 1, TRUE, 0, NOW(6), NOW(6));

INSERT INTO stock_movements (product_id, movement_type, quantity, previous_stock, new_stock, reference_number, notes, movement_date)
SELECT id, 'AJUSTE_ENTRADA', stock, 0, stock, 'INICIAL', 'Stock inicial de utiles escolares del Peru', NOW(6)
FROM products WHERE code IN ('FAB-COL-24', 'MCH-ESC-PER', 'FAB-TAJ-DEP', 'FAB-BOR-BLA', 'VIN-CAR-PAC');

