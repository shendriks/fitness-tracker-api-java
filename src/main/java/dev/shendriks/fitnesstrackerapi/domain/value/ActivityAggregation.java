package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.Builder;

/**
 * Aggregated metrics across a set of activities, including totals and maxima.
 */
@Builder
public record ActivityAggregation(
    long count,
    Distance totalDistance,
    Duration totalDuration,
    Distance maxDistance,
    Duration maxDuration
) {
    /**
     * Creates an empty aggregation with zero values.
     */
    public static ActivityAggregation zero() {
        return new ActivityAggregation(0L, Distance.zero(), Duration.zero(), Distance.zero(), Duration.zero());
    }
}
