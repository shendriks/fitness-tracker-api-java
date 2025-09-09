# Fitness Tracker API

[![Java CI with Gradle](https://github.com/shendriks/fitness-tracker-api-java/actions/workflows/gradle.yml/badge.svg)](https://github.com/shendriks/fitness-tracker-api-java/actions/workflows/gradle.yml)
[![Dependabot Updates](https://github.com/shendriks/fitness-tracker-api-java/actions/workflows/dependabot/dependabot-updates/badge.svg)](https://github.com/shendriks/fitness-tracker-api-java/actions/workflows/dependabot/dependabot-updates)
![Coverage](https://img.shields.io/endpoint?url=https%3A%2F%2Fgist.githubusercontent.com%2Fshendriks%2F03eeb6afeb7203a9623921eeb46576b4%2Fraw%2Fcoverage.json)

An API for tracking fitness activities build with Java and Spring Boot.

## What this API does
This service provides REST endpoints to manage and analyze fitness activities (like runs or rides). Features include:
* Importing and processing activity files (GPX)
* Persisting activities and metadata
* Generating route previews and basic analytics
* Exposing data over HTTP for a web UI or other clients
* Gamification of activity completion with milestones, challenges and trophies

It is intended as a backend you can run to power a fitness‑tracking frontend. It's currently in development and not yet 
ready for production use. The database used is H2, that is deleted on every restart.

## Related projects
- A Nuxt Frontend: https://github.com/shendriks/fitness-tracker-ui-nuxt - use this as the UI that talks to this API.  
- A meta repository: https://github.com/shendriks/fitness-tracker - use this to quickly spin up both the API and the frontend together for demo purposes with docker compose.

## Run locally (withouth docker)
### Prerequisites
- Java 21

Gradle wrapper is included, so you do not need a global Gradle installation.

### Start the API
```bash
# Windows PowerShell / CMD
./gradlew.bat bootRun

# Or, Linux Shell
./gradlew bootRun
```
The application will start on port 8080. Open Swagger UI at http://localhost:8080/swagger-ui/index.html

### Run tests
```bash
# Integration and unit tests
./gradlew.bat check
```

## Run locally with docker compose
to quickly spin up the API and the frontend together with docker compose, use this meta repo: https://github.com/shendriks/fitness-tracker

## Further Documentation
- [Architecture Documentation](./docs/architecture.md)
- [Architecture Decision Log](./docs/adl)

## Note
⚠️ This project is still in development and is not yet ready for production use. Use at your own risk.