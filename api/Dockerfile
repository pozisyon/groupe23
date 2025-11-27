# ---- Build stage ---------------------------------------------------------
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

COPY . .
RUN ./mvnw -B -DskipTests clean package

# ---- Runtime stage -------------------------------------------------------
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copier uniquement le JAR (léger, optimal)
COPY --from=builder /app/target/*.jar app.jar

# Cloud Run écoute sur $PORT
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
