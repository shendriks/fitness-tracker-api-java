# Fitness Tracker API

[![Java CI with Gradle](https://github.com/shendriks/fitness-tracker-api-java/actions/workflows/gradle.yml/badge.svg)](https://github.com/shendriks/fitness-tracker-api-java/actions/workflows/gradle.yml)
[![Dependabot Updates](https://github.com/shendriks/fitness-tracker-api-java/actions/workflows/dependabot/dependabot-updates/badge.svg)](https://github.com/shendriks/fitness-tracker-api-java/actions/workflows/dependabot/dependabot-updates)
![Coverage](https://img.shields.io/endpoint?url=https%3A%2F%2Fgist.githubusercontent.com%2Fshendriks%2F03eeb6afeb7203a9623921eeb46576b4%2Fraw%2Fcoverage.json)

An API for tracking fitness activities

## Setup

There are at least two ways to run the application:

### Docker

You can run the application with Docker Compose:

```bash
docker compose up
```

### Gradle

If you have Java >= 21 and Gradle >= 8.14 installed, you can run the application with:

```
./gradlew bootRun
```

or on Windows:

```
./gradlew.bat bootRun
```

### Swagger UI

When running the application, Swagger UI will be available at http://localhost:8080/swagger-ui/index.html

## Architecture

### Context Diagram

![Context Diagram](./docs/ContextDiagram.png)

## Further Documentation

* [Architecture Decision Log](./docs/adl)
