package dev.shendriks.fitnesstrackerapi.domain.challenge.dto;

import java.time.Instant;
import java.util.Optional;

public record ChallengeResponse(
    String id,
    String name,
    String description,
    Instant startDate,
    Instant endDate,
    Optional<String> imageData,
    boolean hasUserJoined
) {
}
