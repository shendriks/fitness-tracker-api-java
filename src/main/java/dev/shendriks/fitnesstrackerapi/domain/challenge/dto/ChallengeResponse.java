package dev.shendriks.fitnesstrackerapi.domain.challenge.dto;

import java.util.Optional;

public record ChallengeResponse(
    String id,
    String name,
    String description,
    Optional<String> imageData
) {
}
