package com.proyectoexamen.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Clase principal de inicio del Backend en Spring Boot.
 * 
 * Anotaciones:
 * - @SpringBootApplication: Configura la auto-configuración de Spring Boot y el escaneo de componentes.
 * - @EntityScan: Define el paquete base donde Spring buscará las clases anotadas con @Entity.
 * - @EnableJpaRepositories: Define el paquete base donde se encuentran las interfaces JpaRepository.
 */
@SpringBootApplication
@EntityScan("com.proyectoexamen.backend.entity")
@EnableJpaRepositories("com.proyectoexamen.backend.repository")
public class BackendApplication {

	public static void main(String[] args) {
		// Arranca el servidor embebido Tomcat e inicializa el contexto de la aplicación
		SpringApplication.run(BackendApplication.class, args);
	}

}
