# -------- Frontend build (Vite) --------
FROM node:20-bullseye AS frontend-builder
WORKDIR /app/frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# -------- Backend build (Spring Boot) --------
FROM maven:3.9-eclipse-temurin-21 AS backend-builder
WORKDIR /build
COPY backend/pom.xml ./backend/pom.xml
# Pre-fetch dependencies
RUN mvn -q -f backend/pom.xml -DskipTests dependency:go-offline
# Copy backend sources
COPY backend/ ./backend/
# Copy built frontend into Spring's static resources before packaging
RUN rm -rf backend/src/main/resources/static && mkdir -p backend/src/main/resources/static
COPY --from=frontend-builder /app/frontend/dist/ ./backend/src/main/resources/static/
# Package the app
RUN mvn -q -f backend/pom.xml -DskipTests clean package spring-boot:repackage

# -------- Runtime image --------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=backend-builder /build/backend/target/flightlog-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=80.0"
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
