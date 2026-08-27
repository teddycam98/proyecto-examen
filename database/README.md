# Base de datos - Libreria Vico

1. Ejecute `00_create_database.sql` una sola vez con MySQL Workbench.
2. Defina `DB_USERNAME` y `DB_PASSWORD` en el entorno local.
3. Inicie el backend. Flyway aplicara automaticamente las migraciones versionadas de
   `backend/src/main/resources/db/migration`.

No edite una migracion que ya se haya ejecutado. Para cambiar el esquema, agregue una
nueva migracion `V3__descripcion.sql`, `V4__descripcion.sql`, etc.
