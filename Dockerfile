# Base image for building the Spring Boot application
FROM gradle:7.4.2-jdk17 as builder
WORKDIR /build

# Only copy the Gradle configuration files first to leverage Docker cache
COPY build.gradle settings.gradle /build/

# Download dependencies - this layer will be cached if the build.gradle files are not modified
RUN gradle --no-daemon dependencies

# Copy the rest of the application
COPY . /build

# Build the application without running tests
RUN gradle --no-daemon build -x test --parallel

# Base image for running the Spring Boot application
FROM openjdk:17.0-slim
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=builder /build/build/libs/*.jar ./app.jar

# Java Run
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dsun.net.inetaddr.ttl=0", "-jar","app.jar"]