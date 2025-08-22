package dev.shendriks.fitnesstrackerapi.domain.entity;

import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeParticipationId;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeParticipationUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

import java.time.Instant;

public record ChallengeParticipation(
    ChallengeParticipationId id,
    ChallengeParticipationUlid ulid,
    UserId userId,
    Challenge challenge,
    Instant createdAt,
    Instant updatedAt,
    int percentageCompleted
) {
}


