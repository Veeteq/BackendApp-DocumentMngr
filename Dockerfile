# ============================================================
# Runtime stage
# ============================================================
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY documentmngr-app/target/documentmngr-app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]