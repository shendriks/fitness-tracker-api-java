package dev.shendriks.fitnesstrackerapi.domain.service.gpx;

import dev.shendriks.fitnesstrackerapi.domain.exception.WayPointsNotSortedException;
import dev.shendriks.fitnesstrackerapi.domain.service.gpx.distance.DistanceCalculator;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import io.jenetics.jpx.WayPoint;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class KilometerMetricsCalculator {
    public static final int MINIMUM_SIGNIFICANT_DISTANCE_IN_METERS = 100;
    private final DistanceCalculator distanceCalculator;
    private final SpeedCalculator speedCalculator;

    private static boolean isSegmentSignificant(Distance kilometerSegmentDistance) {
        return kilometerSegmentDistance.toMeters() > MINIMUM_SIGNIFICANT_DISTANCE_IN_METERS;
    }

    /**
     * Precondition: wayPoints are sorted by timestamp
     */
    public KilometerMetrics calculateKilometerMetrics(List<WayPoint> wayPoints) {
        List<Speed> kilometerSpeeds = new ArrayList<>();
        List<Pace> kilometerPaces = new ArrayList<>();

        if (wayPoints.size() < 2) {
            return new KilometerMetrics(kilometerSpeeds, kilometerPaces);
        }

        Distance kilometerSegmentDistance = Distance.zero();
        Instant kilometerSegmentStartTime = wayPoints.getFirst().getTime().orElseThrow();
        WayPoint lastPoint = wayPoints.getFirst();

        for (int i = 1; i < wayPoints.size(); i++) {
            WayPoint currentPoint = wayPoints.get(i);

            if (currentPoint.getTime().isEmpty()) {
                continue;
            }

            if (currentPoint.getTime().get().isBefore(lastPoint.getTime().get())) {
                throw new WayPointsNotSortedException();
            }

            Distance distance = distanceCalculator.calculateDistance(lastPoint, currentPoint);
            kilometerSegmentDistance.add(distance);

            if (kilometerSegmentDistance.toMeters() >= 1000.0) {
                Instant kilometerSegmentEndTime = currentPoint.getTime().get();
                Duration segmentDuration = Duration.ofJavaDuration(java.time.Duration
                    .between(kilometerSegmentStartTime, kilometerSegmentEndTime));
                Speed segmentSpeed = speedCalculator.calculateSpeed(kilometerSegmentDistance, segmentDuration);
                Pace segmentPace = segmentSpeed.toPace();

                kilometerSpeeds.add(segmentSpeed);
                kilometerPaces.add(segmentPace);

                kilometerSegmentDistance = Distance.zero();
                kilometerSegmentStartTime = kilometerSegmentEndTime;
            }

            lastPoint = currentPoint;
        }

        if (isSegmentSignificant(kilometerSegmentDistance)) {
            Instant kilometerSegmentEndTime = wayPoints.getLast().getTime().orElseThrow();
            Duration segmentDuration = Duration.ofJavaDuration(java.time.Duration
                .between(kilometerSegmentStartTime, kilometerSegmentEndTime));
            Speed segmentSpeed = speedCalculator.calculateSpeed(kilometerSegmentDistance, segmentDuration);
            Pace segmentPace = segmentSpeed.toPace();

            kilometerSpeeds.add(segmentSpeed);
            kilometerPaces.add(segmentPace);
        }

        return new KilometerMetrics(kilometerSpeeds, kilometerPaces);
    }
}