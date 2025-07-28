package dev.shendriks.fitnesstrackerapi.domain.milestone.dto;

import java.util.Optional;

public record MilestoneResponse(
    String id,
    String name,
    String description,
    Optional<String> imageData,
    boolean isCompleted
) {
}
