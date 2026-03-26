# Use Java 21
FROM eclipse-temurin:21-jdk

# Working directory
WORKDIR /app

# Copy jar
COPY build/libs/demo-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]