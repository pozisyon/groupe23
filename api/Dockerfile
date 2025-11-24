FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

FROM eclipse-temurin:17
WORKDIR /app

COPY --from=build /app/api/target/api-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]


