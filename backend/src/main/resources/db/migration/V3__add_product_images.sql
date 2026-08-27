ALTER TABLE products
    ADD COLUMN image_name VARCHAR(100) NULL AFTER description;

UPDATE products SET image_name = 'demo-lapicero.svg' WHERE code = 'ESC-LAP-AZ';
UPDATE products SET image_name = 'demo-lapiz.svg' WHERE code = 'ESC-LAP-2B';
UPDATE products SET image_name = 'demo-cuaderno.svg' WHERE code = 'ESC-CUA-A4';
UPDATE products SET image_name = 'demo-folder.svg' WHERE code = 'OFI-FOL-MAN';
UPDATE products SET image_name = 'demo-engrapador.svg' WHERE code = 'OFI-ENG-26';
