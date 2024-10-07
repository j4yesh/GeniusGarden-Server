# Use an official Maven image to build the application
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the current directory contents into the container
COPY . .

# Build the application without running tests
RUN mvn clean package -DskipTests

# Use a different image to run the application
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged JAR file from the build image
COPY --from=build /app/target/server-0.0.1-SNAPSHOT.jar demo.jar

# Expose the application port
EXPOSE 8081

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "geniusgarden.jar"]