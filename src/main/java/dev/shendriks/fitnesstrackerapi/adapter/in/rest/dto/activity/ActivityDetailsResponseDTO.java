package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

public record ActivityDetailsResponseDTO(
    @Schema(description = "Unique identifier of the activity", example = "01K9FH8M45HMBBZSCPCR0TFHQ7", type = "string", format = "ulid")
    String id,
    @Schema(implementation = ActivityType.class, description = "The type of activity", example = "running")
    ActivityType activityType,
    @Schema(description = "Duration of the activity in seconds", example = "3600", type = "integer", format = "int64")
    Long duration,
    @Schema(description = "Distance covered during the activity in meters", example = "10000.5", type = "number", format = "double")
    Double distance,
    @Schema(description = "Average speed during the activity in meters per second (m/s)", example = "2.8", type = "number", format = "double")
    Double averageSpeed,
    @Schema(description = "Creation timestamp (RFC3339)", example = "2023-01-01T12:00:00Z", type = "string", format = "date-time")
    Instant createdAt,
    @Schema(description = "Last update timestamp (RFC3339)", example = "2023-01-01T12:30:00Z", type = "string", format = "date-time")
    Instant updatedAt,
    @Schema(description = "Title of the activity", example = "Morning Run", type = "string")
    String title,
    @Schema(description = "Description of the activity", example = "Nice and easy 10k around the park.", type = "string")
    String description,
    @Schema(description = "Start timestamp of the activity (RFC3339)", example = "2023-01-01T11:00:00Z", type = "string", format = "date-time")
    Instant startDate,
    @ArraySchema(schema = @Schema(implementation = GPSPositionResponseDTO.class, description = "Recorded GPS positions for this activity"))
    List<GPSPositionResponseDTO> gpsPositions,
    @ArraySchema(schema = @Schema(description = "Average speeds per kilometer in meters per second (m/s)", type = "number", format = "double"))
    List<Double> kilometerSpeeds,
    @Schema(description = "Total elevation gain in meters", example = "120.5", type = "number", format = "double")
    Double elevationGain,
    @Schema(description = "Total motion time in seconds (excluding pauses)", example = "3500", type = "integer", format = "int64")
    Long motionTime,
    @Schema(description = "Total pausing time in seconds", example = "100", type = "integer", format = "int64")
    Long pausingTime
) {
}
