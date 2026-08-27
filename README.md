# Librería Vico - Sistema de Inventario y Ventas

Sistema web Spring MVC desarrollado para el control de útiles escolares y artículos de escritorio de Librería Vico.

---

## 🏛️ Arquitectura Spring Web MVC (Escuela Superior La Pontificia)

El proyecto implementa la arquitectura por capas y el patrón **Modelo-Vista-Controlador (MVC)** exacto solicitado en el material del curso:

| Pilar MVC | Capa Arquitectónica | Estereotipo Spring | Ubicación en el Código | Responsabilidad |
| :--- | :--- | :--- | :--- | :--- |
| **Vista (V)** | Vista HTML | `HTML / Thymeleaf` | `backend/src/main/resources/vista/` | Interfaz gráfica visible por el usuario (`.html`). |
| **Controlador (C)** | Web Controller | `@Controller` | `com.proyectoexamen.backend.controller` | Recibe las peticiones web (URLs) y responde a la Vista. |
| **Controlador (C)** | Lógica de Negocio | `@Service` | `com.proyectoexamen.backend.service.impl` | Procesa las reglas de negocio y transacciones. |
| **Modelo (M)** | Acceso a Datos (DAO) | `@Repository` | `com.proyectoexamen.backend.repository` | Consulta la base de datos heredando `JpaRepository`. |
| **Modelo (M)** | Entidad Persistente | `@Entity` | `com.proyectoexamen.backend.entity` | Clase Java mapeada directamente a una tabla SQL. |

```text
Cliente (Navegador)
       │
       ▼
   Controlador (@Controller)
       │
       ▼
     Servicio (@Service)
       │
       ▼
   Repositorio (@Repository)
       │
       ▼
     Entidad (@Entity)
       │
       ▼
 Base de Datos (MySQL)
```

---

## 📦 Módulos del Sistema

- **Catálogo:** Gestión completa de categorías, proveedores y productos.
- **Punto de Venta (POS):** Carrito de compras dinámico, buscador por código/nombre y validación de existencias.
- **Compras:** Ingreso de mercadería con incremento automático de stock.
- **Ajustes e Historial (Kardex):** Registro inmutable en `stock_movements` por cada movimiento de inventario.
- **Imágenes:** Almacenamiento seguro de fotografías en formato JPG, PNG o WEBP.
- **Dashboard:** Indicadores clave, ventas del día y alertas de stock mínimo.

---

## ⚙️ Inicio Local

1. Cree la base de datos ejecutando `database/00_create_database.sql` en su servidor MySQL (o deje que Flyway la cree automáticamente).
2. Configure las credenciales en `backend/src/main/resources/application.properties` (`DB_USERNAME` y `DB_PASSWORD`).
3. Desde la carpeta `backend`, ejecute:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
4. Abra su navegador e ingrese a **`http://localhost:8080`**.

---

## 🧪 Pruebas Automatizadas

Para ejecutar la suite de pruebas unitarias y de integración:
```powershell
.\mvnw.cmd test
```
