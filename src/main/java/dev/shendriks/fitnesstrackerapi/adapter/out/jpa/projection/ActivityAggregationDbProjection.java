package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection;

import dev.shendriks.fitnesstrackerapi.domain.value.Distance;
import dev.shendriks.fitnesstrackerapi.domain.value.Duration;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class ActivityAggregationDbProjection {
    private final long count;
    private final Distance totalDistance;
    private final Duration totalDuration;
    private final Distance maxDistance;
    private final Duration maxDuration;

    public ActivityAggregationDbProjection(
        Long count,
        Double totalDistance,
        Double totalDuration,
        Double maxDistance,
        Double maxDuration
    ) {
        this.count = count;
        this.totalDistance = Distance.ofMeters(totalDistance);
        this.totalDuration = Duration.ofSeconds(totalDuration);
        this.maxDistance = Distance.ofMeters(maxDistance);
        this.maxDuration = Duration.ofSeconds(maxDuration);
    }
}
