package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto;

public record AchievementResponseDTO(
    String id,
    String name,
    String description,
    String imageFilePath
) {
}
