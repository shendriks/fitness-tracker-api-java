package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AchievementResponseDTO(
    @Schema(description = "Unique identifier of the achievement", example = "01K9JBT00JGHGGZXX0DCMXCRA2", type = "string", format = "ulid")
    String id,
    @Schema(description = "Name of the achievement", example = "First 5K", type = "string")
    String name,
    @Schema(description = "Description of what the achievement represents", example = "Completed a 5 km run", type = "string")
    String description,
    @Schema(description = "Path or URL to the achievement image", example = "/images/achievements/first-5k.png", type = "string")
    String imageFilePath
) {
}
