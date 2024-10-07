# Use a base image with JDK
FROM openjdk:17-jdk-slim as build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Second stage to create the runtime image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/target/server-0.0.1-SNAPSHOT.jar ./geniusgarden.jar

# Command to run the application
ENTRYPOINT ["java", "-jar", "geniusgarden.jar"]
