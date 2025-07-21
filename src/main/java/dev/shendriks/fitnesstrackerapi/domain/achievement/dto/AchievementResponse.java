package dev.shendriks.fitnesstrackerapi.domain.achievement.dto;

import dev.shendriks.fitnesstrackerapi.domain.activity.dto.ActivityResponse;
import dev.shendriks.fitnesstrackerapi.domain.challenge.dto.ChallengeResponse;

public record AchievementResponse(
    String id,
    ChallengeResponse challenge,
    ActivityResponse activity
) {
}
