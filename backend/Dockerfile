# ============================================
# Multi-stage build для FinPuls Backend (ускорённая версия)
# ============================================

# Stage 1: Build (официальный Maven + Temurin JDK 21)
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Копируем pom.xml и кешируем зависимости
COPY pom.xml .
RUN mvn -B -q -T 1C dependency:go-offline

# Копируем исходники и собираем
COPY src ./src
RUN mvn -B -q -T 1C clean package -DskipTests

# ============================================
# Stage 2: Runtime (официальный Temurin JRE 21)
# ============================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Устанавливаем curl для healthcheck
USER root
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Копируем JAR
COPY --from=build /app/target/*.jar app.jar

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${SERVER_PORT:-8080}/actuator/health || exit 1

ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]
