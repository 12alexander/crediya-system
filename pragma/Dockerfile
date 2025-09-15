FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY applications/app-service/build/libs/*.jar app.jar

RUN mkdir -p logs

EXPOSE 8090

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]