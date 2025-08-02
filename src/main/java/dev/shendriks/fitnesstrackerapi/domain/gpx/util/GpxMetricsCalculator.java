package dev.shendriks.fitnesstrackerapi.domain.gpx.util;

import dev.shendriks.fitnesstrackerapi.domain.gpx.util.distance.DistanceCalculator;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.WayPoint;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Component
public class GpxMetricsCalculator {
    private static final double SPEED_THRESHOLD = 0.5;
    private final GpxWaypointProcessor waypointProcessor;
    private final DistanceCalculator distanceCalculator;

    public GpxMetricsCalculator(GpxWaypointProcessor waypointProcessor, DistanceCalculator distanceCalculator) {
        this.waypointProcessor = waypointProcessor;
        this.distanceCalculator = distanceCalculator;
    }

    public double calculateTotalLength(List<WayPoint> points) {
        if (points == null || points.size() < 2) {
            return 0;
        }

        return IntStream
            .range(1, points.size())
            .mapToDouble(i -> distanceCalculator.calculateDistance(
                points.get(i - 1),
                points.get(i)))
            .sum();
    }

    public long calculateDuration(List<WayPoint> points) {
        Instant firstTime = points.getFirst().getTime().orElse(null);
        Instant lastTime = points.getLast().getTime().orElse(null);

        if (firstTime == null || lastTime == null) {
            return 0;
        }

        return Duration.between(firstTime, lastTime).getSeconds();
    }

    public double calculateSpeed(double totalLength, long duration) {
        return duration > 0 ? totalLength / duration : 0;
    }

    public double calculatePace(double speed) {
        return speed > 0 ? 1000 / speed : 0;
    }

    public double calculateElevationGain(List<WayPoint> points) {
        if (points == null || points.size() < 2) {
            return 0;
        }

        return IntStream.range(1, points.size()).mapToDouble(i -> {
            Optional<Double> elev1 = points.get(i - 1).getElevation().map(Length::doubleValue);
            Optional<Double> elev2 = points.get(i).getElevation().map(Length::doubleValue);

            if (elev1.isEmpty() || elev2.isEmpty()) {
                return 0;
            }
            double elevationDiff = elev2.get() - elev1.get();
            return elevationDiff > 0 ? elevationDiff : 0;
        }).sum();
    }

    public long[] calculateMotionAndPausingTime(List<WayPoint> points) {
        long totalMotionTime = 0;
        long totalPausingTime = 0;

        for (int i = 0; i < points.size() - 1; i++) {
            WayPoint p1 = points.get(i);
            WayPoint p2 = points.get(i + 1);

            if (p1.getTime().isEmpty() || p2.getTime().isEmpty()) {
                continue;
            }

            Instant t1 = p1.getTime().get();
            Instant t2 = p2.getTime().get();

            long timeBetweenPoints = Duration.between(t1, t2).getSeconds();
            double distance = distanceCalculator.calculateDistance(p1, p2);
            double speed = timeBetweenPoints > 0 ? distance / timeBetweenPoints : 0;

            if (speed >= SPEED_THRESHOLD) {
                totalMotionTime += timeBetweenPoints;
            } else {
                totalPausingTime += timeBetweenPoints;
            }
        }

        return new long[]{totalMotionTime, totalPausingTime};
    }
}