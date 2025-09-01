package dev.shendriks.fitnesstrackerapi.application.service.gpx;

import dev.shendriks.fitnesstrackerapi.application.service.gpx.distance.DistanceCalculator;
import dev.shendriks.fitnesstrackerapi.domain.value.Distance;
import dev.shendriks.fitnesstrackerapi.domain.value.Duration;
import dev.shendriks.fitnesstrackerapi.domain.value.MotionAndPausingTime;
import dev.shendriks.fitnesstrackerapi.domain.value.Speed;
import io.jenetics.jpx.WayPoint;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@AllArgsConstructor
public class GpxMetricsCalculator {
    private static final double SPEED_THRESHOLD_METERS_PER_SECOND = 0.5;
    private final DistanceCalculator distanceCalculator;
    private final SpeedCalculator speedCalculator;

    public Distance calculateDistance(List<WayPoint> points) {
        if (points == null || points.size() < 2) {
            return Distance.zero();
        }

        Distance distance = Distance.zero();
        for (var i = 1; i < points.size(); i++) {
            distance.add(distanceCalculator.calculateDistance(
                points.get(i - 1),
                points.get(i)));
        }

        return distance;
    }

    public Duration calculateDuration(List<WayPoint> points) {
        Instant firstTime = points.getFirst().getTime().orElse(null);
        Instant lastTime = points.getLast().getTime().orElse(null);

        if (firstTime == null || lastTime == null) {
            return Duration.zero();
        }

        return Duration.ofJavaDuration(java.time.Duration.between(firstTime, lastTime));
    }

    public Distance calculateElevationGain(List<WayPoint> points) {
        if (points == null || points.size() < 2) {
            return Distance.zero();
        }

        List<Double> nonEmptyElevations = points
            .stream()
            .filter(p -> p.getElevation().isPresent())
            .map(p -> p.getElevation().get().doubleValue())
            .toList();

        Distance elevationGain = Distance.zero();
        for (var i = 1; i < nonEmptyElevations.size(); i++) {
            Double elevation1 = nonEmptyElevations.get(i - 1);
            Double elevation2 = nonEmptyElevations.get(i);
            double elevationDiff = elevation2 - elevation1;
            elevationGain.addMeters(elevationDiff > 0 ? elevationDiff : 0.0);
        }

        return elevationGain;
    }

    public MotionAndPausingTime calculateMotionAndPausingTime(List<WayPoint> points) {
        Duration totalMotionTime = Duration.zero();
        Duration totalPausingTime = Duration.zero();

        List<WayPoint> pointsWithTime = points.stream().filter(p -> p.getTime().isPresent()).toList();

        for (int i = 0; i < pointsWithTime.size() - 1; i++) {
            WayPoint wayPoint1 = pointsWithTime.get(i);
            WayPoint wayPoint2 = pointsWithTime.get(i + 1);

            Instant time1 = wayPoint1.getTime().orElseThrow();
            Instant time2 = wayPoint2.getTime().orElseThrow();

            Duration timeBetweenPoints = Duration.ofJavaDuration(java.time.Duration.between(time1, time2));
            Distance distance = distanceCalculator.calculateDistance(wayPoint1, wayPoint2);
            Speed speed = speedCalculator.calculateSpeed(distance, timeBetweenPoints);

            if (speed.toMetersPerSecond() >= SPEED_THRESHOLD_METERS_PER_SECOND) {
                totalMotionTime.add(timeBetweenPoints);
            } else {
                totalPausingTime.add(timeBetweenPoints);
            }
        }

        return MotionAndPausingTime
            .builder()
            .motionTime(totalMotionTime)
            .pausingTime(totalPausingTime)
            .build();
    }
}
