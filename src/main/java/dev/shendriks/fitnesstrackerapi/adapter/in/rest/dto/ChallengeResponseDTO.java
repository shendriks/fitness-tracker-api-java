package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto;

import java.time.Instant;

public record ChallengeResponseDTO(
    String id,
    String name,
    String description,
    Instant startDate,
    Instant endDate,
    String imageFilePath,
    boolean hasUserJoined
) {
}
