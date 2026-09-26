# Fitness Tracker API

[![Java CI with Gradle](https://github.com/shendriks/fitness-tracker-api-java/actions/workflows/gradle.yml/badge.svg)](https://github.com/shendriks/fitness-tracker-api-java/actions/workflows/gradle.yml)
[![Dependabot Updates](https://github.com/shendriks/fitness-tracker-api-java/actions/workflows/dependabot/dependabot-updates/badge.svg)](https://github.com/shendriks/fitness-tracker-api-java/actions/workflows/dependabot/dependabot-updates)
![Coverage](https://img.shields.io/endpoint?url=https%3A%2F%2Fgist.githubusercontent.com%2Fshendriks%2F03eeb6afeb7203a9623921eeb46576b4%2Fraw%2Fcoverage.json)

An API for tracking fitness activities built with Java and Spring Boot.
Part of the [Fitness Tracker project](https://shendriks.dev/projects/fitness-tracker).

## What this API does
This service provides REST endpoints to manage and analyze fitness activities (like runs or rides). Features include:
* User sign-up and login
* Importing and processing activity files (GPX) as well as manual activity entries
* Persisting activities and metadata to a database
* Generating route previews and basic analytics, like average speed/pace and kilometer splits 
* Gamification of activity completion with milestones, challenges and trophies
* Exposing data over HTTP for a web UI or other clients

It is intended as a backend you can run to power a fitness‑tracking frontend. **It's currently in development and not yet 
ready for production use.** The database used is H2, which is deleted on every restart.

## Related projects
* A Nuxt Frontend: https://github.com/shendriks/fitness-tracker-ui-nuxt - use this as the UI that talks to this API.  

## Run locally (withouth docker)
### Prerequisites
* Java 21

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

## Further Documentation
* [Architecture Documentation](./docs/architecture.md)
* [Architecture Decision Log](./docs/adl/adl.md)

## Note
⚠️ Side-project and self-education experiment. No longer actively developed and not production-ready. Use at your own risk.
