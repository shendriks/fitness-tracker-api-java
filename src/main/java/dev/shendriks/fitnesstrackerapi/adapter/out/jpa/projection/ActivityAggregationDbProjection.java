package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection;

import dev.shendriks.fitnesstrackerapi.domain.value.Distance;
import dev.shendriks.fitnesstrackerapi.domain.value.Duration;
import lombok.EqualsAndHashCode;
import lombok.Getter;

//import java.math.BigDecimal;

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
//        BigDecimal totalDistance,
//        BigDecimal totalDuration,
//        BigDecimal maxDistance,
//        BigDecimal maxDuration
    ) {
        this.count = count;
        this.totalDistance = Distance.ofMeters(totalDistance);
        this.totalDuration = Duration.ofSeconds(totalDuration);
        this.maxDistance = Distance.ofMeters(maxDistance);
        this.maxDuration = Duration.ofSeconds(maxDuration);
//        this.totalDistance = Distance.ofMeters(totalDistance.doubleValue());
//        this.totalDuration = Duration.ofSeconds(totalDuration.doubleValue());
//        this.maxDistance = Distance.ofMeters(maxDistance.doubleValue());
//        this.maxDuration = Duration.ofSeconds(maxDuration.doubleValue());
    }
}
