package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto;

public record MilestoneResponseDTO(
    String id,
    String name,
    String description,
    String imageFilePath,
    boolean isCompleted
) {
}
