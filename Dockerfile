# Use official OpenJDK runtime as base image
FROM openjdk:17-jdk-slim

# Set the working directory - /app as the direcory inside the container where commands will run
WORKDIR /app

# Copy the Gradle build output (fat/uber JAR) into the container
# Make sure you build your project and produce a JAR file
COPY build/libs/webshop-api-all.jar webshop-api.jar

# Expose the port that Ktor app listens on
EXPOSE 8080

# Define the command to run the app
ENTRYPOINT ["java", "-jar", "webshop-api.jar"]