package dev.shendriks.fitnesstrackerapi.adapter.in.rest.milestone.dto;

public record MilestoneResponseDTO(
    String id,
    String name,
    String description,
    String imageFilePath,
    boolean isCompleted
) {
}
