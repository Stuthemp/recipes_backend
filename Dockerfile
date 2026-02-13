FROM gradle:9.1.0-jdk17-alpine as builder
RUN mkdir /app
COPY --chown=gradle:gradle . /app

WORKDIR /app
USER root
RUN gradle build

FROM gradle:9.1.0-jdk17-alpine
ARG JAR_FILE=build/libs/*.jar

# Устанавливаем PostgreSQL клиент (включает pg_dump)
RUN apk add --no-cache postgresql-client

RUN mkdir /app
COPY --from=builder /app/${JAR_FILE} /app/application.jar

EXPOSE 8070
WORKDIR /app

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "application.jar"]