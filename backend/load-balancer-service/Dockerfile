# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY src /app/src
COPY pom.xml /app
RUN mvn -f /app/pom.xml clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
COPY --from=build /app/target/*.jar /app/load-balancer.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "/app/load-balancer.jar"]