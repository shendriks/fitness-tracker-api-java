package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public record GPSPositionResponseDTO(
    @Schema(description = "Timestamp of the GPS position (RFC3339)", example = "2023-01-01T12:00:00Z", type = "string", format = "date-time")
    Instant timestamp,
    @Schema(description = "Latitude in decimal degrees", example = "52.0907", type = "number", format = "double")
    Double latitude,
    @Schema(description = "Longitude in decimal degrees", example = "5.1214", type = "number", format = "double")
    Double longitude,
    @Schema(description = "Altitude in meters above sea level", example = "12.3", type = "number", format = "double")
    Double altitude,
    @Schema(description = "Instantaneous speed in meters per second (m/s)", example = "3.4", type = "number", format = "double")
    Double speed
) {
}
