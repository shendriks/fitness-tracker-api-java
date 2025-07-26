package dev.shendriks.fitnesstrackerapi.domain.trophy.dto;

import dev.shendriks.fitnesstrackerapi.domain.achievement.dto.AchievementResponse;
import dev.shendriks.fitnesstrackerapi.domain.trophy.enums.AchievementType;

import java.time.Instant;

public record TrophyResponse(
    String id,
    AchievementType achievementType,
    AchievementResponse achievement,
    Instant unlockedAt
) {
}
