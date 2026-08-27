# Arquitectura y modelo - Libreria Vico

## Flujo MVC

```text
Navegador / Thymeleaf
        |
        v
Controller  -> valida entrada HTTP y prepara la vista
        |
        v
Service     -> contrato de la logica de negocio
        |
        v
ServiceImpl -> @Service, reglas de negocio y limites transaccionales
        |
        v
Repository  -> consultas JPA
        |
        v
Entity      -> modelo persistente
        |
        v
MySQL 8 / InnoDB
```

Los controladores nunca utilizan repositorios directamente. Los cambios de stock solo se realizan
en servicios transaccionales y se bloquea el producto durante la operacion para evitar ventas
concurrentes por encima de las existencias.

Los paquetes principales respetan la secuencia indicada en el curso: `entity`, `repository`,
`service`, `service.impl`, `controller` y las vistas Thymeleaf. Los paquetes `dto` y `exception`
complementan esa arquitectura para validar formularios y separar los errores de negocio.

## Modelo de datos

```mermaid
erDiagram
    CATEGORIES ||--o{ PRODUCTS : clasifica
    SUPPLIERS ||--o{ PRODUCTS : provee
    SUPPLIERS ||--o{ PURCHASES : atiende
    PURCHASES ||--|{ PURCHASE_ITEMS : contiene
    PRODUCTS ||--o{ PURCHASE_ITEMS : recibe
    SALES ||--|{ SALE_ITEMS : contiene
    PRODUCTS ||--o{ SALE_ITEMS : vende
    PRODUCTS ||--o{ STOCK_MOVEMENTS : registra

    CATEGORIES {
        bigint id PK
        varchar name UK
        boolean active
    }
    SUPPLIERS {
        bigint id PK
        varchar document_number UK
        varchar business_name
        boolean active
    }
    PRODUCTS {
        bigint id PK
        varchar code UK
        varchar name
        varchar image_name
        decimal purchase_price
        decimal sale_price
        int stock
        int minimum_stock
        bigint version
    }
    SALES {
        bigint id PK
        varchar sale_number UK
        datetime sale_date
        decimal total
        varchar status
    }
    PURCHASES {
        bigint id PK
        varchar purchase_number UK
        datetime purchase_date
        decimal total
        varchar status
    }
    STOCK_MOVEMENTS {
        bigint id PK
        varchar movement_type
        int quantity
        int previous_stock
        int new_stock
        varchar reference_number
    }
```

## Reglas principales

1. Una venta no puede dejar un producto con stock negativo.
2. Una compra incrementa stock y actualiza el ultimo costo de compra.
3. Anular una venta restituye todas sus cantidades.
4. Una compra solo se anula si todavia existe stock suficiente para revertirla.
5. Cada cambio de existencias produce un registro inmutable en `stock_movements`.
6. Los productos, categorias y proveedores con historial se desactivan; no se eliminan.
7. Los precios monetarios usan `BigDecimal` y `DECIMAL(12,2)`.
8. Las credenciales se obtienen de variables de entorno y no se versionan.
9. Las imagenes se validan por tipo y tamano, reciben un nombre aleatorio y se guardan fuera del JAR.

## Migraciones

Flyway es el propietario del esquema. `ddl-auto=validate` obliga a Hibernate a comprobar que las
entidades y la base se mantengan alineadas sin modificar tablas silenciosamente.
