package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.HashMap;
import java.util.Map;

public record ActivityStatsResponseDTO(
    @Schema(description = "Aggregated statistics across all activities", implementation = ActivityAggregationResponseDTO.class)
    ActivityAggregationResponseDTO total,
    @Schema(
        description = "Aggregated statistics grouped by activity type",
        implementation = Map.class,
        example = "{\"running\": {\"count\": 5, \"totalDistance\": 21000.0, \"totalDuration\": 7200, \"maxDistance\": 10000.0, \"maxDuration\": 3600}}"
    )
    HashMap<ActivityType, ActivityAggregationResponseDTO> byType
) {
}
