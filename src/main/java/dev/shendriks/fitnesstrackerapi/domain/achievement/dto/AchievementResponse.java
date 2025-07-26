package dev.shendriks.fitnesstrackerapi.domain.achievement.dto;

import java.util.Optional;

public record AchievementResponse(
    String id,
    String name,
    String description,
    Optional<String> imageData
) {
}
