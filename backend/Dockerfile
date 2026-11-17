# Etapa 1: construcción del artefacto con Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src src
RUN mvn -B -q package -DskipTests

# Etapa 2: imagen de ejecución
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/usuarios-api.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
