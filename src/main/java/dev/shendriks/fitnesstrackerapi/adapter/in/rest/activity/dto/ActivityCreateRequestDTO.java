package dev.shendriks.fitnesstrackerapi.adapter.in.rest.activity.dto;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.validation.enums.ValueOfEnum;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ActivityCreateRequestDTO(
    @NotBlank
    @ValueOfEnum(enumClass = ActivityType.class)
    @Schema(implementation = ActivityType.class)
    String activityType,
    @Min(0)
    int duration,
    @Min(0)
    int calories,
    @NotBlank
    String title,
    String description,
    @Min(0)
    int distance,
    Instant startDate
) {
}
