package com.proyectoexamen.backend;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.DriverManager;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;

class DatabaseMigrationTests {

    @Test
    void migrationsCreateTheSchemaAndSeedCatalog() throws Exception {
        String url = "jdbc:h2:mem:migrations;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";
        var result = Flyway.configure()
            .dataSource(url, "sa", "")
            .locations("classpath:db/migration")
            .load()
            .migrate();

        assertThat(result.migrationsExecuted).isEqualTo(5);
        try (var connection = DriverManager.getConnection(url, "sa", "");
             var statement = connection.createStatement();
             var rows = statement.executeQuery("select count(*) from products")) {
            rows.next();
            assertThat(rows.getInt(1)).isEqualTo(18);
        }
    }
}
