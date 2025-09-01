FROM eclipse-temurin:21-jdk

RUN apt-get update && apt-get install -y --no-install-recommends \
    inotify-tools \
  && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle

RUN chmod +x gradlew

RUN ./gradlew --no-daemon dependencies || true

EXPOSE 8080

CMD ["./gradlew", "bootRun", "-x", "test", "--continuous", "--no-daemon", "--watch-fs"]
