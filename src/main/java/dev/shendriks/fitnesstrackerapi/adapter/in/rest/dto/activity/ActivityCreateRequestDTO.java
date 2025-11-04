package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.validation.enums.ValueOfEnum;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ActivityCreateRequestDTO(
    @NotBlank
    @ValueOfEnum(enumClass = ActivityType.class)
    @Schema(implementation = ActivityType.class, description = "The type of activity", example = "running")
    String activityType,
    @Min(0)
    @Max(86400)
    @NotNull
    @Schema(description = "The duration of the activity in seconds", example = "60", type = "integer", format = "int64")
    Long duration,
    @Min(0)
    @Max(100000)
    @NotNull
    @Schema(description = "The distance of the activity in meters", example = "6000.456", type = "number", format = "double")
    Double distance,
    @NotBlank
    @Schema(description = "The title of the activity", example = "Morning Run")
    String title,
    @Schema(description = "A description of the activity (can be empty)", example = "A nice run for the day.")
    @NotNull
    String description,
    @NotNull
    @Schema(description = "The start date of the activity in RFC3339 format", example = "2022-01-01T00:00:00Z", type = "string", format = "date-time")
    Instant startDate
) {
}
