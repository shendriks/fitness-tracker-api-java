package dev.shendriks.fitnesstrackerapi.domain.achievement.dto;

import dev.shendriks.fitnesstrackerapi.domain.challenge.dto.ChallengeResponse;

import java.time.Instant;

public record AchievementResponse(
    String id,
    Instant achievedAt,
    ChallengeResponse challenge
) {
}
