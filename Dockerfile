# Etapa 1: Compilación con Maven y Java 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY backend/pom.xml .
COPY backend/src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Entorno con JDK 21 y fuentes para JasperReports
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Instalar fuentes y utilidades graficas para reportes PDF de JasperReports
RUN apk add --no-cache fontconfig ttf-dejavu

RUN mkdir -p uploads/products

COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -XX:+UseSerialGC -Xmx350m -Xss512k -Djava.awt.headless=true -Dserver.port=${PORT:-8080} -jar app.jar"]
