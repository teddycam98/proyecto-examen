# Etapa 1: Compilación con Maven y Java 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY backend/pom.xml .
COPY backend/src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Entorno de ejecución con JDK 21 y fuentes para JasperReports
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Fuentes y utilidades gráficas para PDFs
RUN apk add --no-cache fontconfig ttf-dejavu

RUN mkdir -p uploads/products

# Copiar el JAR y desempaquetarlo (exploded jar)
# Al desempaquetar las librerías en el classpath del sistema, javac puede encontrar JasperReports sin error
COPY --from=build /app/target/*.jar app.jar
RUN jar -xf app.jar && rm app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -XX:+UseSerialGC -Xmx350m -Xss512k -Djava.awt.headless=true -Dserver.port=${PORT:-8080} -cp '/app/BOOT-INF/classes:/app/BOOT-INF/lib/*' com.proyectoexamen.backend.BackendApplication"]
