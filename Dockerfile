
# Use a base image with Java pre-installed
FROM openjdk:17-jdk-alpine

# Set the default port as an environment variable
ENV APP_PORT 8080

# Set the working directory inside the container
WORKDIR /app

# Copy the .jar file into the container
COPY target/spring-restfull-0.0.1-SNAPSHOT.jar app.jar

# Specify the command to run your application, using the environment variable
CMD ["java", "-jar", "app.jar", "--server.port=${APP_PORT}"]



