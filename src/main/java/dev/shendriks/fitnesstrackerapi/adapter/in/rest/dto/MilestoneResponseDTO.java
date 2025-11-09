package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MilestoneResponseDTO(
    @Schema(description = "Unique identifier of the milestone", example = "01K9JBYC6Z6H8F4Q6PX43G8V24", type = "string", format = "ulid")
    String id,
    @Schema(description = "Name of the milestone", example = "10 Activities", type = "string")
    String name,
    @Schema(description = "Description of the milestone", example = "Complete 10 activities in total", type = "string")
    String description,
    @Schema(description = "Path or URL to the milestone image", example = "/images/milestones/10-activities.png", type = "string")
    String imageFilePath,
    @Schema(description = "Whether the user has completed this milestone", example = "false", type = "boolean")
    boolean isCompleted
) {
}
