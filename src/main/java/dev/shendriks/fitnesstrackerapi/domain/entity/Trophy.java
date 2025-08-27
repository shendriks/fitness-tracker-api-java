package dev.shendriks.fitnesstrackerapi.domain.entity;

import dev.shendriks.fitnesstrackerapi.domain.enums.AchievementType;
import dev.shendriks.fitnesstrackerapi.domain.value.TrophyId;
import dev.shendriks.fitnesstrackerapi.domain.value.TrophyUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.Builder;

import java.time.Instant;

@Builder
public record Trophy(
    TrophyId id,
    TrophyUlid ulid,
    UserId userId,
    Achievement achievement,
    AchievementType achievementType,
    Instant unlockedAt
) {
}
