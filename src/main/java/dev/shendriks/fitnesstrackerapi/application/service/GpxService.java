package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.service.gpx.GpxMetricsCalculator;
import dev.shendriks.fitnesstrackerapi.application.service.gpx.GpxWaypointProcessor;
import dev.shendriks.fitnesstrackerapi.application.service.gpx.KilometerMetricsCalculator;
import dev.shendriks.fitnesstrackerapi.application.service.gpx.SpeedCalculator;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Metadata;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.WayPoint;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Log
@AllArgsConstructor
public class GpxService {
    private final GpxWaypointProcessor waypointProcessor;
    private final GpxMetricsCalculator metricsCalculator;
    private final KilometerMetricsCalculator kilometerMetricsCalculator;
    private final SpeedCalculator speedCalculator;

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

        Distance distance = metricsCalculator.calculateDistance(wayPoints);
        Duration duration = metricsCalculator.calculateDuration(wayPoints);
        Speed speed = speedCalculator.calculateSpeed(distance, duration);
        Pace pace = speed.toPace();
        Distance elevationGain = metricsCalculator.calculateElevationGain(wayPoints);
        MotionAndPausingTime motionAndPausingTime = metricsCalculator.calculateMotionAndPausingTime(wayPoints);

        KilometerMetrics kilometerMetrics = kilometerMetricsCalculator.calculateKilometerMetrics(wayPoints);
        List<Speed> kilometerSpeeds = kilometerMetrics.speeds();
        List<Pace> kilometerPaces = kilometerMetrics.paces();

        List<GPSPositionData> gpsPositions = wayPoints
            .stream()
            .map((wp) -> new GPSPositionData(
                wp.getTime().orElse(Instant.MIN),
                wp.getLatitude().doubleValue(),
                wp.getLongitude().doubleValue(),
                wp.getElevation().flatMap(length -> Optional.of(length.doubleValue()))
            ))
            .toList();

        return new GPSTrackData(
            name,
            gpxTime,
            distance,
            duration,
            speed,
            pace,
            elevationGain,
            motionAndPausingTime.motionTime(),
            motionAndPausingTime.pausingTime(),
            kilometerSpeeds,
            kilometerPaces,
            gpsPositions
        );
    }
}