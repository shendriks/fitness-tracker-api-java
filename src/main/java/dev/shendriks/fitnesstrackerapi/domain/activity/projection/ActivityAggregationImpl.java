package dev.shendriks.fitnesstrackerapi.domain.activity.projection;

public record ActivityAggregationImpl(
    Long getCount, 
    Long getTotalDistance, 
    Long getTotalDuration, 
    Long getMaxDistance,
    Long getMaxDuration
) implements ActivityAggregation {
}
