# ---- Build stage ---------------------------------------------------------
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw -B -DskipTests clean package

# ---- Runtime stage -------------------------------------------------------
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
