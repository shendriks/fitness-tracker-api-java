package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ActivityAggregationResponseDTO(
    @Schema(example = "17", description = "The number of activities", type = "integer", format = "int64")
    long count,
    @Schema(example = "1324.75", description = "The sum of all distances in meters", type = "number", format = "double")
    Double totalDistance,
    @Schema(example = "763.78", description = "The sum of all durations in seconds", type = "integer", format = "int64")
    Long totalDuration,
    @Schema(example = "700.67", description = "The longest distance in meters", type = "number", format = "double")
    Double maxDistance,
    @Schema(example = "363.78", description = "The longest duration in seconds", type = "integer", format = "int64")
    Long maxDuration
) {
}
