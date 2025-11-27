# Build stage
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw -B -DskipTests clean package

# Runtime
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
