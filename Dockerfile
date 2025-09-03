ARG JAVA_VERSION=21

FROM eclipse-temurin:${JAVA_VERSION}-jdk-alpine

WORKDIR /app

RUN --mount=type=bind,source=gradlew,target=gradlew \
    --mount=type=bind,source=gradle,target=gradle \
    --mount=type=bind,source=build.gradle,target=build.gradle \
    --mount=type=bind,source=settings.gradle,target=settings.gradle \
    --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon dependencies

EXPOSE 8080

CMD ["./gradlew", "bootRun"]
