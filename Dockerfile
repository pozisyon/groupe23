# =====================================================================
# 1) FRONTEND (React)
# =====================================================================
FROM node:18 AS frontend-builder
WORKDIR /frontend

COPY frontend/package*.json ./
RUN npm install

COPY frontend/ .
RUN npm run build


# =====================================================================
# 2) BACKEND (Spring Boot multi-module)
# =====================================================================
FROM eclipse-temurin:17-jdk AS backend-builder
WORKDIR /app

# Copier l'ensemble du projet (parent + modules)
COPY . .

# Rendre mvnw exécutable
RUN chmod +x mvnw

# Copier le build React dans le backend Spring Boot
RUN rm -rf backend/src/main/resources/static/*
COPY --from=frontend-builder /frontend/build/ backend/src/main/resources/static/

# Build Maven multi-module (parent + backend)
RUN ./mvnw -B -DskipTests clean package


# =====================================================================
# 3) IMAGE FINALE
# =====================================================================
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=backend-builder /app/backend/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
