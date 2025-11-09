package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto;

import dev.shendriks.fitnesstrackerapi.domain.enums.AchievementType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record TrophyResponseDTO(
    @Schema(description = "Unique identifier of the trophy", example = "01K9JC0AQPZW00EZH282FY196J", type = "string", format = "ulid")
    String id,
    @Schema(implementation = AchievementType.class, description = "Type of achievement for this trophy", example = "milestone")
    AchievementType achievementType,
    @Schema(description = "Achievement details linked to this trophy")
    AchievementResponseDTO achievement,
    @Schema(description = "Timestamp when the trophy was unlocked (RFC3339)", example = "2023-01-05T14:22:00Z", type = "string", format = "date-time")
    Instant unlockedAt
) {
}
