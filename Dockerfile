# ========================
# Bước 1: Build app
# ========================
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ========================
# Bước 2: Chạy app
# ========================
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/iCommerce-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
