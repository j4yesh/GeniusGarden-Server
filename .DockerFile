# Stage 1: Build the application
FROM maven:3.9.5-openjdk-17-slim AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/target/server-0.0.1-SNAPSHOT.jar ./geniusgarden.jar

# Command to run the application
ENTRYPOINT ["java", "-jar", "geniusgarden.jar"]
