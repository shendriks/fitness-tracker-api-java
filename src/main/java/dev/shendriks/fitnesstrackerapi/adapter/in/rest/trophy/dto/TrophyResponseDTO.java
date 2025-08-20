package dev.shendriks.fitnesstrackerapi.adapter.in.rest.trophy.dto;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.achievement.dto.AchievementResponseDTO;
import dev.shendriks.fitnesstrackerapi.domain.enums.AchievementType;

import java.time.Instant;

public record TrophyResponseDTO(
    String id,
    AchievementType achievementType,
    AchievementResponseDTO achievement,
    Instant unlockedAt
) {
}
