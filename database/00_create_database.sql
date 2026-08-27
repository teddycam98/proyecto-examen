-- ========================================================
-- Script Completo de Base de Datos - Libreria Vico
-- Incluye la creacion del schema, tablas, relaciones e inserts iniciales.
-- ========================================================

CREATE DATABASE IF NOT EXISTS libreria_vico
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE libreria_vico;

-- --------------------------------------------------------
-- 1. TABLA: CATEGORIES
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(80) NOT NULL,
    description VARCHAR(250) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT uk_category_name UNIQUE (name)
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- 2. TABLA: SUPPLIERS
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS suppliers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    business_name VARCHAR(120) NOT NULL,
    document_number VARCHAR(11) NULL,
    contact_name VARCHAR(100) NULL,
    phone VARCHAR(20) NULL,
    email VARCHAR(120) NULL,
    address VARCHAR(200) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_suppliers PRIMARY KEY (id),
    CONSTRAINT uk_supplier_document UNIQUE (document_number)
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- 3. TABLA: PRODUCTS
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS products (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(30) NOT NULL,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(300) NULL,
    image_name VARCHAR(100) NULL,
    unit_of_measure VARCHAR(20) NOT NULL,
    purchase_price DECIMAL(12,2) NOT NULL,
    sale_price DECIMAL(12,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    minimum_stock INT NOT NULL DEFAULT 0,
    category_id BIGINT NOT NULL,
    supplier_id BIGINT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_products PRIMARY KEY (id),
    CONSTRAINT uk_product_code UNIQUE (code),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_product_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    CONSTRAINT chk_product_prices CHECK (purchase_price >= 0 AND sale_price > 0),
    CONSTRAINT chk_product_stock CHECK (stock >= 0 AND minimum_stock >= 0)
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- 4. TABLA: SALES
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS sales (
    id BIGINT NOT NULL AUTO_INCREMENT,
    sale_number VARCHAR(25) NOT NULL,
    sale_date DATETIME(6) NOT NULL,
    customer_name VARCHAR(120) NOT NULL,
    customer_document VARCHAR(11) NULL,
    total DECIMAL(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_sales PRIMARY KEY (id),
    CONSTRAINT uk_sale_number UNIQUE (sale_number),
    CONSTRAINT chk_sale_total CHECK (total >= 0)
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- 5. TABLA: SALE_ITEMS
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS sale_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    sale_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    CONSTRAINT pk_sale_items PRIMARY KEY (id),
    CONSTRAINT fk_sale_item_sale FOREIGN KEY (sale_id) REFERENCES sales(id),
    CONSTRAINT fk_sale_item_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT chk_sale_item_values CHECK (quantity > 0 AND unit_price > 0 AND subtotal >= 0)
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- 6. TABLA: PURCHASES
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS purchases (
    id BIGINT NOT NULL AUTO_INCREMENT,
    purchase_number VARCHAR(25) NOT NULL,
    purchase_date DATETIME(6) NOT NULL,
    supplier_id BIGINT NOT NULL,
    document_reference VARCHAR(40) NULL,
    total DECIMAL(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_purchases PRIMARY KEY (id),
    CONSTRAINT uk_purchase_number UNIQUE (purchase_number),
    CONSTRAINT fk_purchase_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    CONSTRAINT chk_purchase_total CHECK (total >= 0)
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- 7. TABLA: PURCHASE_ITEMS
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS purchase_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    purchase_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_cost DECIMAL(12,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    CONSTRAINT pk_purchase_items PRIMARY KEY (id),
    CONSTRAINT fk_purchase_item_purchase FOREIGN KEY (purchase_id) REFERENCES purchases(id),
    CONSTRAINT fk_purchase_item_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT chk_purchase_item_values CHECK (quantity > 0 AND unit_cost > 0 AND subtotal >= 0)
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- 8. TABLA: STOCK_MOVEMENTS
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS stock_movements (
    id BIGINT NOT NULL AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    movement_type VARCHAR(30) NOT NULL,
    quantity INT NOT NULL,
    previous_stock INT NOT NULL,
    new_stock INT NOT NULL,
    reference_number VARCHAR(30) NULL,
    notes VARCHAR(250) NULL,
    movement_date DATETIME(6) NOT NULL,
    CONSTRAINT pk_stock_movements PRIMARY KEY (id),
    CONSTRAINT fk_movement_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT chk_movement_values CHECK (quantity > 0 AND previous_stock >= 0 AND new_stock >= 0)
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- DATOS INICIALES (CATEGORIAS, PROVEEDOR, PRODUCTOS Y STOCK)
-- --------------------------------------------------------

INSERT INTO categories (id, name, description, active, created_at, updated_at) VALUES
    (1, 'Escritura', 'Lapiceros, lapices, plumones y resaltadores', TRUE, NOW(6), NOW(6)),
    (2, 'Cuadernos y papel', 'Cuadernos, blocks, papel y cartulinas', TRUE, NOW(6), NOW(6)),
    (3, 'Organizacion', 'Folders, archivadores, sobres y etiquetas', TRUE, NOW(6), NOW(6)),
    (4, 'Oficina', 'Accesorios y suministros de escritorio', TRUE, NOW(6), NOW(6)),
    (5, 'Arte escolar', 'Colores, temperas, pinceles y manualidades', TRUE, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO suppliers (id, business_name, document_number, contact_name, phone, email, address, active, created_at, updated_at) VALUES
    (1, 'Distribuidora Escolar Demo', '20123456789', 'Contacto comercial', '999999999', 'ventas@proveedor.demo', 'Lima, Peru', TRUE, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE business_name=VALUES(business_name);

INSERT INTO products (id, code, name, description, image_name, unit_of_measure, purchase_price, sale_price, stock, minimum_stock, category_id, supplier_id, active, version, created_at, updated_at) VALUES
    (1, 'ESC-LAP-AZ', 'Lapicero azul', 'Lapicero de tinta azul para uso escolar y oficina', 'demo-lapicero.svg', 'UNIDAD', 0.60, 1.00, 30, 10, 1, 1, TRUE, 0, NOW(6), NOW(6)),
    (2, 'ESC-LAP-2B', 'Lapiz 2B', 'Lapiz de grafito con borrador', 'demo-lapiz.svg', 'UNIDAD', 0.50, 1.00, 24, 8, 1, 1, TRUE, 0, NOW(6), NOW(6)),
    (3, 'ESC-CUA-A4', 'Cuaderno cuadriculado A4', 'Cuaderno de 100 hojas', 'demo-cuaderno.svg', 'UNIDAD', 5.00, 7.50, 15, 5, 2, 1, TRUE, 0, NOW(6), NOW(6)),
    (4, 'OFI-FOL-MAN', 'Folder manila', 'Folder tamano A4', 'demo-folder.svg', 'UNIDAD', 0.40, 0.80, 40, 12, 3, 1, TRUE, 0, NOW(6), NOW(6)),
    (5, 'OFI-ENG-26', 'Engrapador mediano', 'Engrapador para grapas 26/6', 'demo-engrapador.svg', 'UNIDAD', 8.00, 12.50, 8, 3, 4, 1, TRUE, 0, NOW(6), NOW(6)),
    (6, 'ART-COL-12', 'Colores Artesco 12 unid.', 'Caja de 12 colores escolares Artesco de minas suaves y resistentes', 'colores-artesco-12.jpg', 'CAJA', 8.50, 13.00, 40, 10, 5, 1, TRUE, 0, NOW(6), NOW(6)),
    (7, 'STN-CUA-A4', 'Cuaderno Stanford A4 Cuadriculado', 'Cuaderno cosido A4 de 100 hojas cuadriculado Stanford con caratula decorada', 'cuaderno-stanford-a4.jpg', 'UNIDAD', 4.80, 7.20, 60, 15, 2, 1, TRUE, 0, NOW(6), NOW(6)),
    (8, 'FAB-LAP-033', 'Pack Lapiceros Faber-Castell 033 (x3)', 'Set de 3 lapiceros de tinta fina Faber-Castell 033 (Azul, Rojo, Negro)', 'lapiceros-faber-033.jpg', 'PAQUETE', 2.20, 3.80, 100, 20, 1, 1, TRUE, 0, NOW(6), NOW(6)),
    (9, 'ART-TEM-06', 'Tempera Artesco 6 Colores con Pincel', 'Estuche de 6 lavables de 15ml con pincel escolar incluido', 'temperas-artesco-6.jpg', 'JUEGO', 4.50, 7.00, 30, 8, 5, 1, TRUE, 0, NOW(6), NOW(6)),
    (10, 'FAB-PLU-FIE', 'Plumones Faber-Castell Fiesta (x12)', 'Caja de 12 marcadores escolares lavables Faber-Castell Fiesta', 'plumones-faber-fiesta.jpg', 'CAJA', 9.00, 14.50, 35, 10, 1, 1, TRUE, 0, NOW(6), NOW(6)),
    (11, 'ART-REG-30', 'Regla Transparente Artesco 30cm', 'Regla plastica transparente de 30 cm con graduacion en milimetros y pulgadas', 'regla-artesco-30cm.jpg', 'UNIDAD', 0.80, 1.50, 50, 12, 4, 1, TRUE, 0, NOW(6), NOW(6)),
    (12, 'ART-GOM-21', 'Goma en Barra Artesco 21g', 'Barra adhesiva lavable de 21g ideal para papel y cartulina', 'goma-artesco-barra.jpg', 'UNIDAD', 2.50, 4.20, 45, 10, 4, 1, TRUE, 0, NOW(6), NOW(6)),
    (13, 'ART-PLA-12', 'Plastilina Artesco 12 Barritas', 'Caja de plastilina escolar no toxica de 12 barritas multicolores', 'plastilina-artesco-12.jpg', 'CAJA', 3.20, 5.50, 50, 15, 5, 1, TRUE, 0, NOW(6), NOW(6)),
    (14, 'FAB-COL-24', 'Colores Faber-Castell 24 unid.', 'Caja de 24 colores clasicos Faber-Castell escolares', 'colores-faber-24.jpg', 'CAJA', 16.50, 24.00, 30, 8, 5, 1, TRUE, 0, NOW(6), NOW(6)),
    (15, 'MCH-ESC-PER', 'Mochila Escolar Ergonomica', 'Mochila escolar resistente reforzada con tirantes acolchados', 'mochila-escolar.jpg', 'UNIDAD', 35.00, 55.00, 20, 5, 3, 1, TRUE, 0, NOW(6), NOW(6)),
    (16, 'FAB-TAJ-DEP', 'Tajador Faber-Castell con Deposito', 'Tajador doble orificio con deposito para residuos', 'tajador-deposito.jpg', 'UNIDAD', 2.80, 4.50, 60, 15, 4, 1, TRUE, 0, NOW(6), NOW(6)),
    (17, 'FAB-BOR-BLA', 'Borrador Blanco Faber-Castell', 'Borrador sintético blanco libre de PVC', 'borrador-faber.jpg', 'UNIDAD', 1.00, 1.80, 80, 20, 1, 1, TRUE, 0, NOW(6), NOW(6)),
    (18, 'VIN-CAR-PAC', 'Pack Cartulinas y Papel Lustre Vinifan', 'Paquete de cartulinas y papeles escolares surtidos', 'cartulina-pack.jpg', 'PAQUETE', 6.50, 10.50, 40, 10, 2, 1, TRUE, 0, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE code=VALUES(code);

INSERT INTO stock_movements (id, product_id, movement_type, quantity, previous_stock, new_stock, reference_number, notes, movement_date) VALUES
    (1, 1, 'AJUSTE_ENTRADA', 30, 0, 30, 'INICIAL', 'Stock inicial de demostracion', NOW(6)),
    (2, 2, 'AJUSTE_ENTRADA', 24, 0, 24, 'INICIAL', 'Stock inicial de demostracion', NOW(6)),
    (3, 3, 'AJUSTE_ENTRADA', 15, 0, 15, 'INICIAL', 'Stock inicial de demostracion', NOW(6)),
    (4, 4, 'AJUSTE_ENTRADA', 40, 0, 40, 'INICIAL', 'Stock inicial de demostracion', NOW(6)),
    (5, 5, 'AJUSTE_ENTRADA', 8, 0, 8, 'INICIAL', 'Stock inicial de demostracion', NOW(6)),
    (6, 6, 'AJUSTE_ENTRADA', 40, 0, 40, 'INICIAL', 'Stock inicial de utiles escolares del Peru', NOW(6)),
    (7, 7, 'AJUSTE_ENTRADA', 60, 0, 60, 'INICIAL', 'Stock inicial de utiles escolares del Peru', NOW(6)),
    (8, 8, 'AJUSTE_ENTRADA', 100, 0, 100, 'INICIAL', 'Stock inicial de utiles escolares del Peru', NOW(6)),
    (9, 9, 'AJUSTE_ENTRADA', 30, 0, 30, 'INICIAL', 'Stock inicial de utiles escolares del Peru', NOW(6)),
    (10, 10, 'AJUSTE_ENTRADA', 35, 0, 35, 'INICIAL', 'Stock inicial de utiles escolares del Peru', NOW(6)),
    (11, 11, 'AJUSTE_ENTRADA', 50, 0, 50, 'INICIAL', 'Stock inicial de utiles escolares del Peru', NOW(6)),
    (12, 12, 'AJUSTE_ENTRADA', 45, 0, 45, 'INICIAL', 'Stock inicial de utiles escolares del Peru', NOW(6)),
    (13, 13, 'AJUSTE_ENTRADA', 50, 0, 50, 'INICIAL', 'Stock inicial de utiles escolares del Peru', NOW(6)),
    (14, 14, 'AJUSTE_ENTRADA', 30, 0, 30, 'INICIAL', 'Stock inicial de utiles escolares del Peru', NOW(6)),
    (15, 15, 'AJUSTE_ENTRADA', 20, 0, 20, 'INICIAL', 'Stock inicial de utiles escolares del Peru', NOW(6)),
    (16, 16, 'AJUSTE_ENTRADA', 60, 0, 60, 'INICIAL', 'Stock inicial de utiles escolares del Peru', NOW(6)),
    (17, 17, 'AJUSTE_ENTRADA', 80, 0, 80, 'INICIAL', 'Stock inicial de utiles escolares del Peru', NOW(6)),
    (18, 18, 'AJUSTE_ENTRADA', 40, 0, 40, 'INICIAL', 'Stock inicial de utiles escolares del Peru', NOW(6))
ON DUPLICATE KEY UPDATE movement_type=VALUES(movement_type);
