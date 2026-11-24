# ==============================================================================
# Multi-stage Dockerfile for Spring Boot 3 / Java 17 Employee Management System
# ==============================================================================

# Stage 1: Build stage with Maven and OpenJDK 17
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Cache dependencies by copying pom.xml first
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source code and compile
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Lean, secure production runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create a non-root dedicated user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy built executable jar from builder stage
COPY --from=builder /app/target/employee-management-system-*.jar app.jar

# Render injects PORT dynamically (default 8080)
ENV PORT=8080
EXPOSE 8080

# Production JVM optimizations for containerized environments
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dserver.port=${PORT}", \
    "-jar", "app.jar"]
