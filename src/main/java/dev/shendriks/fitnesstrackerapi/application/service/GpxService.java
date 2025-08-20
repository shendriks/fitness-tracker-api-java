package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.service.gpx.GpxMetricsCalculator;
import dev.shendriks.fitnesstrackerapi.application.service.gpx.GpxWaypointProcessor;
import dev.shendriks.fitnesstrackerapi.application.service.gpx.KilometerMetricsCalculator;
import dev.shendriks.fitnesstrackerapi.domain.value.GPSPositionData;
import dev.shendriks.fitnesstrackerapi.domain.value.GPSTrackData;
import dev.shendriks.fitnesstrackerapi.domain.value.KilometerMetrics;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Metadata;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.WayPoint;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Log
public class GpxService {
    private final GpxWaypointProcessor waypointProcessor;
    private final GpxMetricsCalculator metricsCalculator;
    private final KilometerMetricsCalculator kilometerMetricsCalculator;

    public GpxService(
        GpxWaypointProcessor waypointProcessor,
        GpxMetricsCalculator metricsCalculator,
        KilometerMetricsCalculator kilometerMetricsCalculator
    ) {
        this.waypointProcessor = waypointProcessor;
        this.metricsCalculator = metricsCalculator;
        this.kilometerMetricsCalculator = kilometerMetricsCalculator;
    }

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

        double totalLength = metricsCalculator.calculateTotalLength(wayPoints);
        long duration = metricsCalculator.calculateDuration(wayPoints);
        double speed = metricsCalculator.calculateSpeed(totalLength, duration);
        double pace = metricsCalculator.calculatePace(speed);
        double elevationGain = metricsCalculator.calculateElevationGain(wayPoints);
        long[] motionAndPausingTime = metricsCalculator.calculateMotionAndPausingTime(wayPoints);
        long motionTime = motionAndPausingTime[0];
        long pausingTime = motionAndPausingTime[1];

        KilometerMetrics kilometerMetrics = kilometerMetricsCalculator.calculateKilometerMetrics(wayPoints);
        List<Double> kilometerSpeeds = kilometerMetrics.speeds();
        List<Double> kilometerPaces = kilometerMetrics.paces();

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
            totalLength,
            duration,
            speed,
            pace,
            elevationGain,
            motionTime,
            pausingTime,
            kilometerSpeeds,
            kilometerPaces,
            gpsPositions
        );
    }
}