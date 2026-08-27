# Librería Vico - Sistema de Inventario y Ventas

Sistema web Spring Boot + Thymeleaf desarrollado para el control de útiles escolares y artículos de escritorio de Librería Vico.

---

## 📁 Estructura General del Proyecto

```text
proyecto-examen/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/proyectoexamen/backend/
│   │   │   │       ├── controller/        # Recibe peticiones web y encamina a la vista
│   │   │   │       ├── dto/               # Transporte y validación de datos de formularios
│   │   │   │       ├── entity/            # Entidades Java mapeadas a tablas de MySQL
│   │   │   │       ├── exception/         # Manejo centralizado de errores de negocio
│   │   │   │       ├── repository/        # Acceso a datos heredando JpaRepository (DAO)
│   │   │   │       ├── service/           # Lógica de negocio e inventarios
│   │   │   │       └── BackendApplication.java
│   │   │   └── resources/
│   │   │       ├── db/
│   │   │       │   └── migration/         # Migraciones Flyway (V1 a V5)
│   │   │       ├── static/                # Recursos estáticos web
│   │   │       │   ├── css/
│   │   │       │   ├── js/
│   │   │       │   └── img/
│   │   │       ├── vista/                 # Vistas HTML motorizadas por Thymeleaf
│   │   │       │   ├── categories/
│   │   │       │   ├── fragments/
│   │   │       │   ├── products/
│   │   │       │   ├── purchases/
│   │   │       │   ├── sales/
│   │   │       │   ├── stock/
│   │   │       │   ├── suppliers/
│   │   │       │   ├── dashboard.html
│   │   │       │   └── error.html
│   │   │       └── application.properties
│   │   └── test/                          # Pruebas unitarias e integración
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
├── database/                              # Scripts SQL para MySQL
├── docs/                                  # Documentación del proyecto
├── .gitignore
└── README.md
```

### 📌 Notas Importantes sobre la Arquitectura:
- **`backend/`**: Contiene la aplicación Spring Boot monolítica.
- **`src/main/java`**: Contiene el código fuente Java dividido en capas (`controller`, `service`, `repository`, `entity`, `dto`, `exception`).
- **`controller`**: Recibe las solicitudes web (URLs) y responde enviando el modelo a la vista.
- **`service`**: Contiene las reglas de negocio, control transaccional e inventarios.
- **`repository`**: Capa DAO de acceso a datos que extiende `JpaRepository`.
- **`entity`**: Representa las tablas de la base de datos MySQL como clases Java.
- **`src/main/resources/vista`**: Contiene las plantillas de Vistas HTML5 de Thymeleaf.
- **`src/main/resources/static`**: Contiene los recursos estáticos (CSS, JavaScript e imágenes).
- **`database/`**: Contiene los scripts de creación e importación para MySQL (`00_create_database.sql`).
- **`docs/`**: Contiene la documentación técnica y arquitectónica.
- **`target/`**: Carpeta generada automáticamente por Maven al compilar. **No se versiona en Git** (está ignorada en `.gitignore`).
- **No existe un frontend separado**: La interfaz se renderiza en el servidor mediante Thymeleaf integrado en Spring Boot.

---

## 🏛️ Arquitectura Spring Web MVC (Escuela Superior La Pontificia)

| Pilar MVC | Capa Arquitectónica | Estereotipo Spring | Ubicación en el Código | Responsabilidad |
| :--- | :--- | :--- | :--- | :--- |
| **Vista (V)** | Vista HTML | `HTML / Thymeleaf` | `backend/src/main/resources/vista/` | Interfaz gráfica visible por el usuario (`.html`). |
| **Controlador (C)** | Web Controller | `@Controller` | `com.proyectoexamen.backend.controller` | Recibe las peticiones web (URLs) y responde a la Vista. |
| **Controlador (C)** | Lógica de Negocio | `@Service` | `com.proyectoexamen.backend.service.impl` | Procesa las reglas de negocio y transacciones. |
| **Modelo (M)** | Acceso a Datos (DAO) | `@Repository` | `com.proyectoexamen.backend.repository` | Consulta la base de datos heredando `JpaRepository`. |
| **Modelo (M)** | Entidad Persistente | `@Entity` | `com.proyectoexamen.backend.entity` | Clase Java mapeada directamente a una tabla SQL. |

---

## ⚙️ Inicio Local

1. Cree la base de datos ejecutando `database/00_create_database.sql` en su servidor MySQL (o deje que Flyway la cree automáticamente al arrancar).
2. Configure las credenciales en `backend/src/main/resources/application.properties` (ej. `DB_USERNAME` y `DB_PASSWORD`).
3. Desde la carpeta `backend`, ejecute:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
4. Abra su navegador e ingrese a **`http://localhost:8080`**.

---

## 🧪 Pruebas Automatizadas

Para ejecutar la suite de pruebas unitarias y de integración:
```powershell
.\mvnw.cmd clean test
```
