package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.Distance;
import dev.shendriks.fitnesstrackerapi.domain.value.Duration;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class ActivityTypeAggregationDbProjection {
    ActivityType type;
    long count;
    Distance totalDistance;
    Duration totalDuration;
    Distance maxDistance;
    Duration maxDuration;

    public ActivityTypeAggregationDbProjection(
        String type,
        Long count,
        Double totalDistance,
        Double totalDuration,
        Double maxDistance,
        Double maxDuration
    ) {
        this.type = ActivityType.fromString(type);
        this.count = count;
        this.totalDistance = Distance.ofMeters(totalDistance);
        this.totalDuration = Duration.ofSeconds(totalDuration);
        this.maxDistance = Distance.ofMeters(maxDistance);
        this.maxDuration = Duration.ofSeconds(maxDuration);
    }
}
