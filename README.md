# 📚 Librería Vico - Sistema Web de Inventario y Ventas

> **Sistema de Gestión Comercial y Control de Inventarios**  
> 🌐 **Demostración en Vivo en la Nube:** [https://proyecto-examen.onrender.com/](https://proyecto-examen.onrender.com/)

---

## 🎯 ¿Qué es este proyecto? 

**Librería Vico** es una aplicación web integral desarrollada para automatizar y optimizar la administración de una librería y papelería comercial. Resuelve los problemas típicos del registro manual: descuadres de caja, falta de control en el stock, pérdidas por productos agotados y lentitud al atender.

### Puntos clave para sustentar:
1. **Control de Stock en Tiempo Real:** Cada venta descuenta automáticamente la cantidad vendida del inventario, impidiendo ventas de productos sin stock disponible.
2. **Alertas de Stock Mínimo:** El sistema calcula y resalta visualmente los productos próximos a agotarse para anticipar compras de reposición.
3. **Flujo de Compras y Proveedores:** Permite abastecer el inventario, asociar compras a proveedores formales y registrar costos unitarios.
4. **Reportes Profesionales en PDF:** Generación instantánea de reportes de inventario valorizado y reportes de ventas listos para imprimir mediante **JasperReports**.

---

## 🌐 Enlace del Sistema en Vivo (Despliegue Cloud)

* **Enlace Público:** [https://proyecto-examen.onrender.com/](https://proyecto-examen.onrender.com/)
* **Alojamiento Web:** Render.com (Contenedor Docker con Java 21)
* **Base de Datos en la Nube:** TiDB Cloud Serverless (MySQL 8)
* **Seguridad:** Conexión cifrada HTTPS / SSL

> **Nota:** Al estar alojado en el plan gratuito en la nube, si el sistema pasa más de 15 minutos sin recibir visitas entra en reposo automático. Al abrir el enlace, solo demorará unos 30 segundos en reactivarse.

---

## 🛠️ Tecnologías Utilizadas (Explicadas en sencillo)

| Componente | Tecnología | ¿Por qué se utilizó? |
| :--- | :--- | :--- |
| **Lenguaje Principal** | **Java 21 (LTS)** | Versión moderna de soporte extendido, garantiza alto rendimiento y estabilidad. |
| **Framework Backend** | **Spring Boot** | Estándar en la industria para crear aplicaciones empresariales robustas y configurables. |
| **Arquitectura** | **MVC (Modelo - Vista - Controlador)** | Separa la presentación, las reglas de negocio y el acceso a datos para un código limpio. |
| **Base de Datos** | **MySQL 8** (TiDB Cloud) | Base de datos relacional con soporte transaccional ACID y motor InnoDB. |
| **Persistencia / ORM** | **Spring Data JPA / Hibernate** | Facilita las consultas a base de datos mediante entidades Java, evitando SQL manual repetitivo. |
| **Control de Versiones BD** | **Flyway** | Ejecuta scripts de migración automáticos para crear tablas y sembrar productos iniciales. |
| **Motor de Vistas** | **Thymeleaf + HTML5 / CSS3 / Bootstrap** | Renderiza vistas del lado del servidor de forma dinámica, rápida y adaptable a móviles. |
| **Módulo de Reportes** | **JasperReports 7** | Motor para diseñar y renderizar reportes ejecutivos en formato PDF. |
| **Contenedor y Cloud** | **Docker + Render** | Permite empaquetar la aplicación con su entorno exacto y levantarla 24/7 en internet. |

---

## 🧩 Módulos del Sistema

* **📊 Panel de Control (`/`):** Métricas del valor total del inventario en Soles (S/), total facturado del día, número de productos activos y alertas de reposición.
* **📦 Catálogo de Productos (`/productos`):** Listado de útiles escolares y de oficina peruanos (cuadernos Stanford, plumones y colores Faber-Castell, cartulinas Vinifan, etc.) con sus fotos, categorías, precios de costo, precios de venta y stock actual.
* **🧾 Módulo de Ventas (`/ventas`):** Emisión de comprobantes (Boletas y Facturas), control del cliente, detalle de ítems y validación estricta de stock disponible.
* **📥 Módulo de Compras (`/compras`):** Registro de ingreso de mercadería desde proveedores con actualización automática de existencias.
* **🔄 Movimientos de Kardex (`/inventario/movimientos`):** Trazabilidad completa de entradas, salidas y ajustes manuales por merma o auditoría.
* **📄 Reportes Ejecutivos (`/reportes`):**
  - **Inventario Valorizado (PDF):** Reporte formal con existencias, costos unitarios, valorización total e indicadores de reposición.
  - **Ventas por Rango de Fechas (PDF):** Reporte de ingresos, detalle por cliente y estado de comprobantes.

---

## 🏛️ Arquitectura del Software (Flujo de Datos)

El sistema implementa una arquitectura por capas estricta:

```text
[ Navegador Web del Usuario ]
               │
               ▼
   1. Capa Controlador (Controller)
      Valida los parámetros de la solicitud HTTP y define la vista a mostrar.
               │
               ▼
   2. Capa de Servicio (Service / ServiceImpl)
      Aplica las reglas del negocio (validar stock antes de vender, calcular totales).
               │
               ▼
   3. Capa de Datos (Repository / DAO)
      Hereda de JpaRepository para realizar operaciones seguras en base de datos.
               │
               ▼
   4. Capa de Entidad (Entity)
      Clases Java mapeadas directamente a las tablas relacionales.
               │
               ▼
      [ Base de Datos MySQL ]
```

> **Argumento para el docente:** Los controladores **nunca** acceden directamente a la base de datos; todas las operaciones pasan por servicios transaccionales con anotaciones `@Transactional`, protegiendo la consistencia de los datos ante posibles fallos o ventas concurrentes.

---

## 💻 Instrucciones para Ejecutar en Local

Si el docente o evaluador solicita probar el proyecto en una computadora local:

### 1. Prerrequisitos
* Tener instalado **Java 21** o superior.
* Tener instalado y activo un servidor **MySQL 8**.

### 2. Configurar la base de datos
En el archivo `backend/src/main/resources/application.properties`, verificar las credenciales de conexión:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/libreria_vico?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima
spring.datasource.username=root
spring.datasource.password=root
```

### 3. Ejecutar el proyecto
Abrir una terminal en la carpeta `backend`:
```powershell
# En Windows:
.\mvnw.cmd spring-boot:run

# En Linux / macOS:
./mvnw spring-boot:run
```

### 4. Acceder al sistema
Abrir en el navegador:
👉 `http://localhost:8080`

*(Flyway se encargará de crear automáticamente la base de datos `libreria_vico`, todas las tablas y los 18 productos peruanos iniciales).*

---

## 👤 Información del Proyecto
* **Autor:** Teddy Camasca
* **Repositorio Oficial:** [https://github.com/teddycam98/proyecto-examen](https://github.com/teddycam98/proyecto-examen)
