package dev.shendriks.fitnesstrackerapi.domain.activity.dto;

import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.validation.enums.ValueOfEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ActivityRequest(
    @NotBlank
    String username,
    @NotBlank
    @Pattern(regexp = "^(walking|running|swimming|mountain_biking|cycling)$")
    @ValueOfEnum(enumClass = ActivityType.class)
    String activityType,
    @Min(0)
    int duration,
    @Min(0)
    int calories
) {
}
