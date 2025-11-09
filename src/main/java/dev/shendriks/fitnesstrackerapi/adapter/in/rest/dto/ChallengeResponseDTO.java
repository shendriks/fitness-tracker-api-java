package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public record ChallengeResponseDTO(
    @Schema(description = "Unique identifier of the challenge", example = "01K9JBX8QJZ8SGHRQ4FVPP13JP", type = "string", format = "ulid")
    String id,
    @Schema(description = "Name of the challenge", example = "January Distance Challenge", type = "string")
    String name,
    @Schema(description = "Description of the challenge", example = "Run at least 100 km in January", type = "string")
    String description,
    @Schema(description = "Start date/time of the challenge (RFC3339)", example = "2023-01-01T00:00:00Z", type = "string", format = "date-time")
    Instant startDate,
    @Schema(description = "End date/time of the challenge (RFC3339)", example = "2023-01-31T23:59:59Z", type = "string", format = "date-time")
    Instant endDate,
    @Schema(description = "Path or URL to the challenge image", example = "/images/challenges/jan-distance.png", type = "string")
    String imageFilePath,
    @Schema(description = "Whether the current user has joined this challenge", example = "true", type = "boolean")
    boolean hasUserJoined
) {
}
