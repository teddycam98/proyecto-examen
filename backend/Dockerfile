# Etapa 1: Compilación con Maven y Java 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Entorno de ejecución ligero con JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN mkdir -p uploads/products

COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -XX:+UseSerialGC -Xmx350m -Xss512k -Dserver.port=${PORT:-8080} -jar app.jar"]
