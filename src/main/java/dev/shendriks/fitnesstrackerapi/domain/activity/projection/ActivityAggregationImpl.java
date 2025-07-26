package dev.shendriks.fitnesstrackerapi.domain.activity.projection;

public record ActivityAggregationImpl(
    long getCount,
    long getTotalDistance,
    long getTotalDuration,
    long getMaxDistance,
    long getMaxDuration
) implements ActivityAggregation {
    public ActivityAggregationImpl() {
        this(0L, 0L, 0L, 0L, 0L);
    }
}
