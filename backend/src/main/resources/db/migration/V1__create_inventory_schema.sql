CREATE TABLE categories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(80) NOT NULL,
    description VARCHAR(250) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT uk_category_name UNIQUE (name)
) ENGINE=InnoDB;

CREATE TABLE suppliers (
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

CREATE TABLE products (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(30) NOT NULL,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(300) NULL,
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

CREATE INDEX idx_product_name ON products(name);
CREATE INDEX idx_product_category ON products(category_id);
CREATE INDEX idx_product_supplier ON products(supplier_id);

CREATE TABLE sales (
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

CREATE TABLE sale_items (
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

CREATE INDEX idx_sale_date ON sales(sale_date);
CREATE INDEX idx_sale_item_sale ON sale_items(sale_id);
CREATE INDEX idx_sale_item_product ON sale_items(product_id);

CREATE TABLE purchases (
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

CREATE TABLE purchase_items (
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

CREATE INDEX idx_purchase_date ON purchases(purchase_date);
CREATE INDEX idx_purchase_supplier ON purchases(supplier_id);
CREATE INDEX idx_purchase_item_purchase ON purchase_items(purchase_id);

CREATE TABLE stock_movements (
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

CREATE INDEX idx_movement_date ON stock_movements(movement_date);
CREATE INDEX idx_movement_product ON stock_movements(product_id);
