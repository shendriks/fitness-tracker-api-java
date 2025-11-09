package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.validation.enums.ValueOfEnum;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ActivityUpdateRequestDTO(
    @NotBlank
    @ValueOfEnum(enumClass = ActivityType.class)
    @Schema(implementation = ActivityType.class, description = "The type of activity", example = "walking")
    String activityType,
    @NotBlank
    @Schema(description = "The title of the activity", example = "Evening Walk", type = "string")
    String title,
    @Schema(description = "Optional description of the activity", example = "Walked the dog around the neighborhood.", type = "string")
    String description
) {
}
