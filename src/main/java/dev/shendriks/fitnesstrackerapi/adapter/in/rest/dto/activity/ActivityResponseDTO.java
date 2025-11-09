package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record ActivityResponseDTO(
    @Schema(description = "Unique identifier of the activity", example = "01K98363N8VC7WF0PK52TX9X1T", type = "string", format = "ulid")
    String id,
    @Schema(implementation = ActivityType.class, description = "The type of activity", example = "running")
    ActivityType activityType,
    @Schema(description = "Duration of the activity in seconds", example = "1800", type = "integer", format = "int64")
    Long duration,
    @Schema(description = "Distance of the activity in meters", example = "5000.0", type = "number", format = "double")
    Double distance,
    @Schema(description = "Average speed in meters per second (m/s)", example = "2.7", type = "number", format = "double")
    Double averageSpeed,
    @Schema(description = "Creation timestamp (RFC3339)", example = "2023-01-01T12:00:00Z", type = "string", format = "date-time")
    Instant createdAt,
    @Schema(description = "Last update timestamp (RFC3339)", example = "2023-01-01T12:10:00Z", type = "string", format = "date-time")
    Instant updatedAt,
    @Schema(description = "Title of the activity", example = "Lunch Run", type = "string")
    String title,
    @Schema(description = "Description of the activity", example = "Short 5k around the office.", type = "string")
    String description,
    @Schema(description = "Start timestamp of the activity (RFC3339)", example = "2023-01-01T11:30:00Z", type = "string", format = "date-time")
    Instant startDate,
    @Schema(
        description = "Base64-encoded track preview image",
        example = "iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAYAAABytg0kAAAAAXNSR0IArs4c6QAAABdJREFUCJkFwQEBAAAAgiD7P5ogqKyCDlXFCPlryZ/bAAAAAElFTkSuQmCC",
        type = "string",
        format = "byte",
        nullable = true
    )
    String trackPreviewImage
) {
}
