package dev.shendriks.fitnesstrackerapi.adapter.in.rest.achievement.dto;

public record AchievementResponseDTO(
    String id,
    String name,
    String description,
    String imageFilePath
) {
}
