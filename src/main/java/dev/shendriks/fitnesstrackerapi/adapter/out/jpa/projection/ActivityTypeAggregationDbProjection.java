package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import lombok.Builder;

@Builder
public record ActivityTypeAggregationDbProjection(
    ActivityType type,
    long count,
    long totalDistance,
    long totalDuration,
    long maxDistance,
    long maxDuration
) {
}
