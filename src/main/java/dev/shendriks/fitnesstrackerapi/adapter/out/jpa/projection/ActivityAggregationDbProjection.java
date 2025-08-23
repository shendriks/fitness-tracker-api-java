package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection;

import lombok.Builder;

@Builder
public record ActivityAggregationDbProjection(
    long count,
    long totalDistance,
    long totalDuration,
    long maxDistance,
    long maxDuration
) {
}
