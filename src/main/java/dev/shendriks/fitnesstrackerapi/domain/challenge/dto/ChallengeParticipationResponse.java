package dev.shendriks.fitnesstrackerapi.domain.challenge.dto;

import java.time.Instant;

public record ChallengeParticipationResponse(
    String id,
    ChallengeResponse challenge,
    Instant joinedAt,
    Integer percentageCompleted
) {
}
