FROM maven:3.9-eclipse-temurin-17

WORKDIR /app

COPY pom.xml .
COPY src ./src

# Create the storage directory
RUN mkdir -p /app/data

# Compile the application
RUN mvn clean compile

# Set the environment variable for storage (can be overridden in docker-compose)
ENV STORAGE_PATH=/app/data/

# Run the application
CMD ["mvn", "javafx:run"]
