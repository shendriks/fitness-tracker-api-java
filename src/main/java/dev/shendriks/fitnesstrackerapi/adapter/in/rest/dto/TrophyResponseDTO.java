package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto;

import dev.shendriks.fitnesstrackerapi.domain.enums.AchievementType;

import java.time.Instant;

public record TrophyResponseDTO(
    String id,
    AchievementType achievementType,
    AchievementResponseDTO achievement,
    Instant unlockedAt
) {
}
