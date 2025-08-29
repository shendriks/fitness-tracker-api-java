package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.Builder;

@Builder
public record ActivityAggregation(
    long count,
    Distance totalDistance,
    Duration totalDuration,
    Distance maxDistance,
    Duration maxDuration
) {
    public static ActivityAggregation zero() {
        return new ActivityAggregation(0L, Distance.zero(), Duration.zero(), Distance.zero(), Duration.zero());
    }
}
