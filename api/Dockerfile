# -------------------------
# 🛠 STAGE 1 — Build with Maven
# -------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Copier uniquement les pom.xml pour profiter du cache Docker
WORKDIR /build

COPY ./pom.xml ./pom.xml
COPY ../core/pom.xml ../core/pom.xml

# Téléchargement des dépendances (cache Maven)
RUN mvn -B dependency:go-offline

# Copier le projet complet
COPY . .
COPY ../core ../core

# Build de Spring Boot
RUN mvn -B -DskipTests clean package


# -------------------------
# 🏃 STAGE 2 — Runtime image
# -------------------------
FROM eclipse-temurin:17-jdk AS runtime

WORKDIR /app

# Copier le JAR depuis le stage Maven
COPY --from=build /build/target/api-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
