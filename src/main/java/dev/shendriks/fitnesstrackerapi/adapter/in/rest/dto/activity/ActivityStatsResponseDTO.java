package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;

import java.util.HashMap;

public record ActivityStatsResponseDTO(
    ActivityAggregationResponseDTO total,
    HashMap<ActivityType, ActivityAggregationResponseDTO> byType
) {
}
