package dev.shendriks.fitnesstrackerapi.domain.gpx.util;

import dev.shendriks.fitnesstrackerapi.domain.gpx.model.KilometerMetrics;
import dev.shendriks.fitnesstrackerapi.domain.gpx.util.distance.DistanceCalculator;
import io.jenetics.jpx.WayPoint;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class KilometerMetricsCalculator {
    private final DistanceCalculator distanceCalculator;

    public KilometerMetricsCalculator(DistanceCalculator distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }

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
        Instant kilometerSegmentStartTime = sortedPoints.get(0).getTime().get();
        WayPoint lastPoint = sortedPoints.get(0);

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
            Instant kilometerSegmentEndTime = sortedPoints.get(sortedPoints.size() - 1).getTime().get();
            long segmentDuration = Duration.between(kilometerSegmentStartTime, kilometerSegmentEndTime).getSeconds();
            double segmentSpeed = segmentDuration > 0 ? kilometerSegmentDistance / segmentDuration : 0;
            double segmentPace = segmentSpeed > 0 ? 1000 / segmentSpeed : 0;

            kilometerSpeeds.add(segmentSpeed);
            kilometerPaces.add(segmentPace);
        }

        return new KilometerMetrics(kilometerSpeeds, kilometerPaces);
    }
}