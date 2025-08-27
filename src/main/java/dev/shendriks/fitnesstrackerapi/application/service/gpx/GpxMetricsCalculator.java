package dev.shendriks.fitnesstrackerapi.application.service.gpx;

import dev.shendriks.fitnesstrackerapi.application.service.gpx.distance.DistanceCalculator;
import dev.shendriks.fitnesstrackerapi.domain.value.MotionAndPausingTime;
import io.jenetics.jpx.WayPoint;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

@Component
@AllArgsConstructor
public class GpxMetricsCalculator {
    private static final double SPEED_THRESHOLD_METERS_PER_SECOND = 0.5;
    private final DistanceCalculator distanceCalculator;

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

        List<Double> nonEmptyElevations = points
            .stream()
            .filter(p -> p.getElevation().isPresent())
            .map(p -> p.getElevation().get().doubleValue())
            .toList();

        return IntStream.range(1, nonEmptyElevations.size()).mapToDouble(i -> {
            double elevation1 = nonEmptyElevations.get(i - 1);
            double elevation2 = nonEmptyElevations.get(i);
            double elevationDiff = elevation2 - elevation1;
            return elevationDiff > 0 ? elevationDiff : 0;
        }).sum();
    }

    public MotionAndPausingTime calculateMotionAndPausingTime(List<WayPoint> points) {
        long totalMotionTime = 0;
        long totalPausingTime = 0;

        List<WayPoint> pointsWithTime = points.stream().filter(p -> p.getTime().isPresent()).toList();

        for (int i = 0; i < pointsWithTime.size() - 1; i++) {
            WayPoint wayPoint1 = pointsWithTime.get(i);
            WayPoint wayPoint2 = pointsWithTime.get(i + 1);

            Instant time1 = wayPoint1.getTime().orElseThrow();
            Instant time2 = wayPoint2.getTime().orElseThrow();

            long timeBetweenPoints = Duration.between(time1, time2).getSeconds();
            double distance = distanceCalculator.calculateDistance(wayPoint1, wayPoint2);
            double speed = timeBetweenPoints > 0 ? distance / timeBetweenPoints : 0;

            if (speed >= SPEED_THRESHOLD_METERS_PER_SECOND) {
                totalMotionTime += timeBetweenPoints;
            } else {
                totalPausingTime += timeBetweenPoints;
            }
        }

        return MotionAndPausingTime
            .builder()
            .motionTime(totalMotionTime)
            .pausingTime(totalPausingTime)
            .build();
    }
}
