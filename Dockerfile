# Packages the JAR that Jenkins already built and tested (Maven or Gradle).
# Local use:  ./mvnw -B clean package && cp target/javaapp-1.0.0.jar app.jar && docker build -t javaapp:local .
FROM eclipse-temurin:17-jre-alpine
RUN apk upgrade --no-cache && addgroup -S app && adduser -S app -G app
USER app
WORKDIR /app
COPY app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
