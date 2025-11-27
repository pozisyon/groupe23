# =====================================================================
# 1) FRONTEND BUILD (React)
# =====================================================================
FROM node:20.19.0 AS frontend-builder
WORKDIR /frontend

COPY frontend/package*.json ./
RUN npm install

COPY frontend/ .
RUN npm run build


# =====================================================================
# 2) BACKEND MULTI-MODULE MAVEN BUILD
# =====================================================================
FROM eclipse-temurin:17-jdk AS backend-builder
WORKDIR /app

# Copier tout le projet Maven (parent + modules)
COPY . .

# Donner permission à mvnw
RUN chmod +x mvnw

# Copier le frontend dans le module API (serveur web)
RUN rm -rf api/src/main/resources/static/*
COPY --from=frontend-builder /frontend/dist/ api/src/main/resources/static/

RUN mkdir -p api/src/main/frontend && echo "skip frontend build" > api/src/main/frontend/.skip

# Build Maven multi-module (parent → api/core/tchat)
RUN ./mvnw -B -DskipTests clean package


# =====================================================================
# 3) RUNTIME IMAGE
# =====================================================================
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copier le jar du module API (module final executable)
COPY --from=backend-builder /app/api/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
