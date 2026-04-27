
# Etapa de compilación
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Etapa de ejecución (imagen final ~200MB en lugar de ~800MB)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/springbank-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -q --spider http://localhost:8080/api/v1/auth/health 2>/dev/null || exit 1
CMD ["java", "-jar", "app.jar"]
