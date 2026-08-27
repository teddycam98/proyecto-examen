INSERT INTO categories (id, name, description, active, created_at, updated_at) VALUES
    (1, 'Escritura', 'Lapiceros, lapices, plumones y resaltadores', TRUE, NOW(6), NOW(6)),
    (2, 'Cuadernos y papel', 'Cuadernos, blocks, papel y cartulinas', TRUE, NOW(6), NOW(6)),
    (3, 'Organizacion', 'Folders, archivadores, sobres y etiquetas', TRUE, NOW(6), NOW(6)),
    (4, 'Oficina', 'Accesorios y suministros de escritorio', TRUE, NOW(6), NOW(6)),
    (5, 'Arte escolar', 'Colores, temperas, pinceles y manualidades', TRUE, NOW(6), NOW(6));

INSERT INTO suppliers (id, business_name, document_number, contact_name, phone, email, address, active, created_at, updated_at) VALUES
    (1, 'Distribuidora Escolar Demo', '20123456789', 'Contacto comercial', '999999999', 'ventas@proveedor.demo', 'Lima, Peru', TRUE, NOW(6), NOW(6));

INSERT INTO products (id, code, name, description, unit_of_measure, purchase_price, sale_price, stock,
                      minimum_stock, category_id, supplier_id, active, version, created_at, updated_at) VALUES
    (1, 'ESC-LAP-AZ', 'Lapicero azul', 'Lapicero de tinta azul para uso escolar y oficina', 'UNIDAD', 0.60, 1.00, 30, 10, 1, 1, TRUE, 0, NOW(6), NOW(6)),
    (2, 'ESC-LAP-2B', 'Lapiz 2B', 'Lapiz de grafito con borrador', 'UNIDAD', 0.50, 1.00, 24, 8, 1, 1, TRUE, 0, NOW(6), NOW(6)),
    (3, 'ESC-CUA-A4', 'Cuaderno cuadriculado A4', 'Cuaderno de 100 hojas', 'UNIDAD', 5.00, 7.50, 15, 5, 2, 1, TRUE, 0, NOW(6), NOW(6)),
    (4, 'OFI-FOL-MAN', 'Folder manila', 'Folder tamano A4', 'UNIDAD', 0.40, 0.80, 40, 12, 3, 1, TRUE, 0, NOW(6), NOW(6)),
    (5, 'OFI-ENG-26', 'Engrapador mediano', 'Engrapador para grapas 26/6', 'UNIDAD', 8.00, 12.50, 8, 3, 4, 1, TRUE, 0, NOW(6), NOW(6));

INSERT INTO stock_movements (product_id, movement_type, quantity, previous_stock, new_stock,
                             reference_number, notes, movement_date) VALUES
    (1, 'AJUSTE_ENTRADA', 30, 0, 30, 'INICIAL', 'Stock inicial de demostracion', NOW(6)),
    (2, 'AJUSTE_ENTRADA', 24, 0, 24, 'INICIAL', 'Stock inicial de demostracion', NOW(6)),
    (3, 'AJUSTE_ENTRADA', 15, 0, 15, 'INICIAL', 'Stock inicial de demostracion', NOW(6)),
    (4, 'AJUSTE_ENTRADA', 40, 0, 40, 'INICIAL', 'Stock inicial de demostracion', NOW(6)),
    (5, 'AJUSTE_ENTRADA', 8, 0, 8, 'INICIAL', 'Stock inicial de demostracion', NOW(6));
