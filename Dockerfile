# Build stage
FROM gradle:9.1.0-jdk17-alpine AS builder
WORKDIR /app
COPY . .
RUN gradle build -x test --no-daemon

# Runtime stage (optimized)
FROM eclipse-temurin:17-jre-alpine
ARG JAR_FILE=build/libs/*.jar

# Install PostgreSQL client tools properly
RUN apk update && \
    apk add --no-cache postgresql-client && \
    which pg_dump && \
    pg_dump --version

WORKDIR /app
COPY --from=builder /app/${JAR_FILE} application.jar

# Create backup directory with proper permissions
RUN mkdir -p /tmp/backups && chmod 755 /tmp/backups

EXPOSE 8070
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "application.jar"]