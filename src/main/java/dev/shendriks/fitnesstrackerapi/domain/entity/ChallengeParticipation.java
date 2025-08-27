package dev.shendriks.fitnesstrackerapi.domain.entity;

import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeParticipationId;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeParticipationUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.Builder;

import java.time.Instant;

@Builder
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


