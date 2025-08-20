package dev.shendriks.fitnesstrackerapi.application.service.gpx;

import dev.shendriks.fitnesstrackerapi.application.service.gpx.distance.DistanceCalculator;
import dev.shendriks.fitnesstrackerapi.domain.value.KilometerMetrics;
import io.jenetics.jpx.WayPoint;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class KilometerMetricsCalculator {
    private final DistanceCalculator distanceCalculator;

    private static boolean isSegmentSignificant(double kilometerSegmentDistance) {
        return kilometerSegmentDistance > 100;
    }

    public KilometerMetrics calculateKilometerMetrics(List<WayPoint> sortedPoints) {
        List<Double> kilometerSpeeds = new ArrayList<>();
        List<Double> kilometerPaces = new ArrayList<>();

        if (sortedPoints.size() < 2) {
            return new KilometerMetrics(kilometerSpeeds, kilometerPaces);
        }

        double kilometerSegmentDistance = 0;
        Instant kilometerSegmentStartTime = sortedPoints.getFirst().getTime().orElseThrow();
        WayPoint lastPoint = sortedPoints.getFirst();

        for (int i = 1; i < sortedPoints.size(); i++) {
            WayPoint currentPoint = sortedPoints.get(i);

            if (currentPoint.getTime().isEmpty()) {
                continue;
            }

            double distance = distanceCalculator.calculateDistance(lastPoint, currentPoint);
            kilometerSegmentDistance += distance;

            if (kilometerSegmentDistance >= 1000) {
                Instant kilometerSegmentEndTime = currentPoint.getTime().get();
                long segmentDuration = Duration.between(kilometerSegmentStartTime, kilometerSegmentEndTime).getSeconds();
                double segmentSpeed = segmentDuration > 0 ? kilometerSegmentDistance / segmentDuration : 0;
                double segmentPace = segmentSpeed > 0 ? 1000 / segmentSpeed : 0;

                kilometerSpeeds.add(segmentSpeed);
                kilometerPaces.add(segmentPace);

                kilometerSegmentDistance = 0;
                kilometerSegmentStartTime = kilometerSegmentEndTime;
            }

            lastPoint = currentPoint;
        }

        if (isSegmentSignificant(kilometerSegmentDistance)) {
            Instant kilometerSegmentEndTime = sortedPoints.getLast().getTime().orElseThrow();
            long segmentDuration = Duration.between(kilometerSegmentStartTime, kilometerSegmentEndTime).getSeconds();
            double segmentSpeed = segmentDuration > 0 ? kilometerSegmentDistance / segmentDuration : 0;
            double segmentPace = segmentSpeed > 0 ? 1000 / segmentSpeed : 0;

            kilometerSpeeds.add(segmentSpeed);
            kilometerPaces.add(segmentPace);
        }

        return new KilometerMetrics(kilometerSpeeds, kilometerPaces);
    }
}