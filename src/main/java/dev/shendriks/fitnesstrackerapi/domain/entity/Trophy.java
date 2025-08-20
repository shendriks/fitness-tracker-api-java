package dev.shendriks.fitnesstrackerapi.domain.entity;

import dev.shendriks.fitnesstrackerapi.domain.enums.AchievementType;
import dev.shendriks.fitnesstrackerapi.domain.value.TrophyId;
import dev.shendriks.fitnesstrackerapi.domain.value.TrophyUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

import java.time.Instant;

public record Trophy(
    TrophyId id,
    TrophyUlid ulid,
    UserId userId,
    Achievement achievement,
    AchievementType achievementType,
    Instant unlockedAt
) {
}
