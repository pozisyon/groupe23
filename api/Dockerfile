# Étape 1 : build complet Maven
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build

# Copie de tout le projet (parent, core, api, etc.)
COPY . .

# Compilation à partir du pom parent à la racine
RUN mvn -f pom.xml clean package -DskipTests

# Étape 2 : image d’exécution légère
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copie du jar API uniquement
COPY --from=build /build/api/target/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
