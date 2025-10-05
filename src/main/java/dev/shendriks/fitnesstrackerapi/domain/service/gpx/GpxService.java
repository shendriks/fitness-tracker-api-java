package dev.shendriks.fitnesstrackerapi.domain.service.gpx;

import dev.shendriks.fitnesstrackerapi.domain.service.gpx.distance.DistanceCalculator;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Metadata;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.WayPoint;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GpxService {
    private final GpxWaypointProcessor waypointProcessor;
    private final GpxMetricsCalculator metricsCalculator;
    private final KilometerMetricsCalculator kilometerMetricsCalculator;
    private final SpeedCalculator speedCalculator;
    private final DistanceCalculator distanceCalculator;

    private static Optional<String> getName(GPX gpx) {
        return gpx
            .getMetadata()
            .flatMap(Metadata::getName)
            .or(() -> gpx
                .tracks()
                .findFirst()
                .flatMap(Track::getName));
    }

    public GPSTrackData processGpxFile(Path file) throws IOException {
        GPX gpx = GPX.read(file);
        return calculateMetrics(gpx);
    }

    private GPSTrackData calculateMetrics(GPX gpx) {
        List<WayPoint> wayPoints = waypointProcessor.getAllWayPointsOrderedByTime(gpx);
        Optional<Instant> gpxTime = gpx.getMetadata().flatMap(Metadata::getTime).or(() -> wayPoints.getFirst().getTime());
        String name = getName(gpx).orElse("");
        MotionAndPausingTime motionAndPausingTime = metricsCalculator.calculateMotionAndPausingTime(wayPoints);
        List<Speed> kilometerSpeeds = kilometerMetricsCalculator.calculateKilometerSpeeds(wayPoints);

        Distance overallDistance = Distance.zero();
        Duration overallDuration = Duration.zero();
        Distance elevationGain = Distance.zero();
        List<GPSPosition> gpsPositions = new ArrayList<>();

        WayPoint previousPoint = null;
        for (WayPoint wayPoint : wayPoints) {
            Speed speed = Speed.zero();
            if (previousPoint != null) {
                Distance distance = distanceCalculator.calculateDistance(previousPoint, wayPoint);
                Duration duration = calculateDuration(previousPoint, wayPoint);
                speed = speedCalculator.calculateSpeed(distance, duration);
                overallDistance.add(distance);
                overallDuration.add(duration);
                if (wayPoint.getElevation().isPresent() && previousPoint.getElevation().isPresent()) {
                    double diff = wayPoint.getElevation().get().doubleValue() - previousPoint.getElevation().get().doubleValue();
                    if (diff > 0) {
                        elevationGain.addMeters(diff);
                    }
                }
            }

            gpsPositions.add(new GPSPosition(
                wayPoint.getTime().orElse(Instant.MIN),
                wayPoint.getLatitude().doubleValue(),
                wayPoint.getLongitude().doubleValue(),
                wayPoint.getElevation().flatMap(elevation -> Optional.of(elevation.doubleValue())).orElse(null),
                speed
            ));

            previousPoint = wayPoint;
        }
        
        Speed averageSpeed = speedCalculator.calculateSpeed(overallDistance, overallDuration);

        return new GPSTrackData(
            name,
            gpxTime,
            overallDistance,
            overallDuration,
            averageSpeed,
            elevationGain,
            motionAndPausingTime.motionTime(),
            motionAndPausingTime.pausingTime(),
            kilometerSpeeds,
            gpsPositions
        );
    }

    private Duration calculateDuration(WayPoint point1, WayPoint point2) {
        Instant t0 = point1.getTime().orElse(null);
        Instant t1 = point2.getTime().orElse(null);

        if (t0 == null || t1 == null) {
            return Duration.zero();
        }

        return Duration.ofJavaDuration(java.time.Duration.between(t0, t1));
    }
}