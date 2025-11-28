# Use OpenJDK image
FROM eclipse-temurin:21-jdk-jammy

# Set working directory
WORKDIR /app

# Copy Maven build output
COPY target/monitoring-service-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8082

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
