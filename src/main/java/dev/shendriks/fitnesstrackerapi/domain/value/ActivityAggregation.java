package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.Builder;

@Builder
public record ActivityAggregation(
    long count,
    long totalDistance,
    long totalDuration,
    long maxDistance,
    long maxDuration
) {
    public static ActivityAggregation zero() {
        return new ActivityAggregation(0L, 0L, 0L, 0L, 0L);
    }
}
