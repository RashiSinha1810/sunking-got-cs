# Use Maven to build the app
FROM maven:3.9.4-eclipse-temurin-21 AS builder

WORKDIR /app
COPY . .

# Build the app
RUN mvn clean package -DskipTests

# Use a lightweight JDK image to run the built JAR
FROM openjdk:21-jdk-slim

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# # Use an official OpenJDK runtime as a parent image
# FROM openjdk:21-jdk-slim

# # Set the working directory in the container
# WORKDIR /app

# # Copy the built jar file into the container
# COPY target/got-0.0.1.jar app.jar

# # Expose the port the application runs on
# EXPOSE 8080

# # Command to run the application
# ENTRYPOINT ["java", "-jar", "app.jar"]