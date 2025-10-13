package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity;

import lombok.Builder;

@Builder
public record ActivityAggregationResponseDTO(
    long count,
    Double totalDistance,
    Long totalDuration,
    Double maxDistance,
    Long maxDuration
) {
}
