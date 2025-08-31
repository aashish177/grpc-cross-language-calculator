FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy gradle files from java-server directory
COPY java-server/build.gradle .
COPY java-server/gradle gradle
COPY java-server/gradlew .

# Make gradlew executable
RUN chmod +x gradlew

# Copy proto file
COPY proto/calculator.proto src/main/proto/

# Copy source code
COPY java-server/src src

# Build the application
RUN ./gradlew build

# Expose port
EXPOSE 50051

# Run the server
CMD ["./gradlew", "run"]