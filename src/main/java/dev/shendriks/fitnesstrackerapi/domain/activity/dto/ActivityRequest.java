package dev.shendriks.fitnesstrackerapi.domain.activity.dto;

import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.validation.enums.ValueOfEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ActivityRequest(
    @NotBlank
    @ValueOfEnum(enumClass = ActivityType.class)
    @Schema(implementation = ActivityType.class)
    String activityType,
    @Min(0)
    int duration,
    @Min(0)
    int calories
) {
}
