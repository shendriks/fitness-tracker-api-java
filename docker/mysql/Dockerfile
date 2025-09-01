# Simple MySQL image for local development
# Use MySQL Community Server 8.4 LTS
FROM mysql:8.4

# Set default environment variables via build-time suggestion (can be overridden at runtime)
# These are not used at build time but document defaults
ENV MYSQL_ROOT_PASSWORD=root \
    MYSQL_DATABASE=fitnessdb

# Expose MySQL port
EXPOSE 3306

# Use default entrypoint/cmd from the base image
