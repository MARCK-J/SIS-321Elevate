# --- ETAPA 1: BUILD DEL FRONTEND (VUE.JS) ---
FROM node:20-alpine AS frontend-build-stage
WORKDIR /app/FRONTEND

# Copiar package.json y package-lock.json para instalar dependencias primero
COPY FRONTEND/package.json FRONTEND/package-lock.json ./

# Instalar dependencias
RUN npm install

# Copiar el resto del código del FRONTEND
COPY FRONTEND/ .

# Construir la aplicación Vue para producción
# El resultado se copiará a 'dist'
RUN npm run build

# --- ETAPA 2: BUILD DEL BACKEND (SPRING BOOT) ---
FROM maven:4-openjdk-17 AS backend-build-stage
WORKDIR /SIS-321ELEVATE/BACKEND

# Copiar el pom.xml o build.gradle para resolver dependencias
COPY BACKEND/pom.xml .

# Descargar las dependencias de Maven
RUN mvn dependency:go-offline -B

# Copiar el resto del código del backend
COPY BACKEND/src src/

# Copiar los archivos estáticos de Vue.js construidos a la ubicación de Spring Boot
# Asegúrate de que esta ruta coincida con donde Spring Boot busca los archivos estáticos.
# Por defecto, Spring Boot busca en src/main/resources/static/
COPY --from=frontend-build-stage /SIS-321ELEVATE/FRONTEND src/

# Construir el proyecto Spring Boot (creará el JAR ejecutable)
RUN mvn package -DskipTests

# --- ETAPA 3: IMAGEN FINAL PARA EJECUCIÓN ---
FROM openjdk:17-jdk-slim AS final-stage

# Configurar el directorio de trabajo
WORKDIR /SIS-321ELEVATE

# Copiar el JAR ejecutable del backend desde la etapa de build del backend
# El nombre del JAR puede variar, revisa tu target/ o build/libs/
ARG JAR_FILE=BACKEND/target/*.jar
COPY --from=backend-build-stage ${JAR_FILE} app.jar

# Exponer el puerto por defecto de Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]