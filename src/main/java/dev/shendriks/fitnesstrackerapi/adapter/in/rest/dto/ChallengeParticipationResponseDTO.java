package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public record ChallengeParticipationResponseDTO(
    @Schema(description = "Unique identifier of the participation", example = "01K9JBVXVMNMQHPHR87HZZSPKS", type = "string", format = "ulid")
    String id,
    @Schema(description = "The challenge this participation refers to")
    ChallengeResponseDTO challenge,
    @Schema(description = "Timestamp when the user joined the challenge (RFC3339)", example = "2023-01-10T08:00:00Z", type = "string", format = "date-time")
    Instant joinedAt,
    @Schema(description = "Percentage of progress in the challenge", example = "42", type = "integer", format = "int32")
    Integer percentageCompleted
) {
}
