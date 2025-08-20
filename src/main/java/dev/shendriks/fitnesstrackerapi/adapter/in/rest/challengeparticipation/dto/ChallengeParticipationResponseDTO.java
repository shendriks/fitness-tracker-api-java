package dev.shendriks.fitnesstrackerapi.adapter.in.rest.challengeparticipation.dto;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.challenge.dto.ChallengeResponseDTO;

import java.time.Instant;

public record ChallengeParticipationResponseDTO(
    String id,
    ChallengeResponseDTO challenge,
    Instant joinedAt,
    Integer percentageCompleted
) {
}
