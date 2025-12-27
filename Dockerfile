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
# 2) BACKEND MAVEN MULTI-MODULE BUILD
# =====================================================================
FROM eclipse-temurin:17-jdk AS backend-builder
WORKDIR /app

# Copier UNIQUEMENT le Maven Wrapper racine
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copier les POMs des modules (cache Maven)
COPY api/pom.xml api/pom.xml
COPY core/pom.xml core/pom.xml

RUN chmod +x mvnw
RUN ./mvnw -B -DskipTests dependency:go-offline

# Copier maintenant TOUT le code
COPY api api
COPY core core
# ajoute ici d'autres modules si nécessaire (chat, security, etc.)

# Injecter le frontend buildé
RUN rm -rf api/src/main/resources/static/*
COPY --from=frontend-builder /frontend/dist/ api/src/main/resources/static/

# Build final
RUN ./mvnw -B -DskipTests clean package


# =====================================================================
# 3) RUNTIME IMAGE
# =====================================================================
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=backend-builder /app/api/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
