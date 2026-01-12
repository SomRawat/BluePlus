# Build stage
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /app

# Copy gradle files
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

# Copy source code
COPY src src

# Build the application
RUN chmod +x gradlew
RUN ./gradlew clean build -x test

# Runtime stage
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]