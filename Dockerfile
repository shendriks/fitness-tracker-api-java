ARG JAVA_VERSION=21

FROM eclipse-temurin:${JAVA_VERSION}-jdk-alpine

WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle

RUN    --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon dependencies

EXPOSE 8080

CMD ["./gradlew", "bootRun"]
