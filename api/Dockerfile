FROM eclipse-temurin:17-jdk AS runtime

WORKDIR /app

# On copie le JAR déjà construit
COPY target/api-1.0.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
