# ─────────────────────────────────────────────
# Stage 1: Build
# ─────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Cache dependencies trước khi copy source
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source và build
COPY src ./src
RUN ./mvnw package -DskipTests -B

# ─────────────────────────────────────────────
# Stage 2: Runtime
# ─────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

# Tạo non-root user
RUN addgroup -S cinehub && adduser -S cinehub -G cinehub

COPY --from=builder /app/target/*.jar app.jar

RUN chown cinehub:cinehub app.jar

USER cinehub

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
