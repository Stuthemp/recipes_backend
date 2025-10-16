FROM gradle:jdk17 as builder
RUN mkdir /app
COPY --chown=gradle:gradle . /app

WORKDIR /app
USER root
RUN gradle clean build

FROM openjdk:17
ARG JAR_FILE=build/libs/*.jar

RUN mkdir /app
COPY --from=builder /app/${JAR_FILE} /app/application.jar

EXPOSE 8070
WORKDIR /app

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "application.jar"]
