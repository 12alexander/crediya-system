FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar el JAR compilado
COPY applications/app-service/build/libs/*.jar app.jar

# Crear directorio para logs
RUN mkdir -p logs

# Exponer puerto
EXPOSE 8090

# Comando de ejecución con optimizaciones básicas para contenedor
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]