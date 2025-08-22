package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto;

import java.time.Instant;

public record ChallengeParticipationResponseDTO(
    String id,
    ChallengeResponseDTO challenge,
    Instant joinedAt,
    Integer percentageCompleted
) {
}
