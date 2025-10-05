package dev.shendriks.fitnesstrackerapi.domain.service.gpx;

import dev.shendriks.fitnesstrackerapi.domain.value.*;
import dev.shendriks.fitnesstrackerapi.infrastructure.Constant;
import io.jenetics.jpx.WayPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GpxServiceTest {
    @TempDir
    Path tempDir;

    private static String gpxWithMetadata(String name, String timeIso) {
        return """
            <gpx version="1.1" creator="test" xmlns="http://www.topografix.com/GPX/1/1">
              <metadata>
                <name>%s</name>
                <time>%s</time>
              </metadata>
              <trk>
                <name>Dummy Track</name>
                <trkseg>
                  <trkpt lat="0" lon="0"/>
                </trkseg>
              </trk>
            </gpx>
            """.formatted(name, timeIso);
    }

    private static String minimalGpx() {
        return """
            <gpx version="1.1" creator="test" xmlns="http://www.topografix.com/GPX/1/1">
            </gpx>
            """;
    }

    @Test
    void processGpxFile_withMetadataNameAndTime_mapsAllFieldsFromCalculatorsAndMetadataNameTime() throws IOException {
        GpxWaypointProcessor waypointProcessor = Mockito.mock(GpxWaypointProcessor.class);
        GpxMetricsCalculator metricsCalculator = Mockito.mock(GpxMetricsCalculator.class);
        KilometerMetricsCalculator kilometerMetricsCalculator = Mockito.mock(KilometerMetricsCalculator.class);
        SpeedCalculator speedCalculator = Mockito.mock(SpeedCalculator.class);
        GpxService service = new GpxService(waypointProcessor, metricsCalculator, kilometerMetricsCalculator, speedCalculator);

        String name = "Morning Run";
        Instant metaTime = Instant.parse("2025-08-22T10:00:00Z");
        Path gpxPath = tempDir.resolve("with_meta.gpx");
        Files.writeString(gpxPath, gpxWithMetadata(name, metaTime.toString()));

        List<WayPoint> wayPoints = List.of(
            WayPoint
                .builder()
                .lat(50.0).lon(5.0)
                .time(Instant.parse("2025-08-22T10:00:10Z"))
                .ele(100.5)
                .build(),
            WayPoint.builder()
                .lat(50.001).lon(5.002)
                .time(Instant.parse("2025-08-22T10:05:10Z"))
                .build()
        );
        when(waypointProcessor.getAllWayPointsOrderedByTime(any())).thenReturn(wayPoints);
        when(metricsCalculator.calculateDistance(wayPoints)).thenReturn(Distance.ofMeters(12345.6));
        when(metricsCalculator.calculateDuration(wayPoints)).thenReturn(Duration.ofSeconds(3000L));
        when(speedCalculator.calculateSpeed(Distance.ofMeters(12345.6), Duration.ofSeconds(3000L)))
            .thenReturn(Speed.ofMetersPerSecond(4.1152));
        when(metricsCalculator.calculateElevationGain(wayPoints)).thenReturn(Distance.ofMeters(200.0));
        when(metricsCalculator.calculateMotionAndPausingTime(wayPoints)).thenReturn(
            MotionAndPausingTime
                .builder()
                .motionTime(Duration.ofSeconds(2500L))
                .pausingTime(Duration.ofSeconds(500L))
                .build()
        );
        when(kilometerMetricsCalculator.calculateKilometerMetrics(wayPoints)).thenReturn(
            new KilometerMetrics(
                List.of(Speed.ofMetersPerSecond(10.0), Speed.ofMetersPerSecond(9.5)),
                List.of(Pace.ofSecondsPerKilometer(6.0), Pace.ofSecondsPerKilometer(6.3))
            )
        );

        GPSTrackData actualGPSTrackData = service.processGpxFile(gpxPath);

        assertInstanceOf(GPSTrackData.class, actualGPSTrackData);
        assertEquals(name, actualGPSTrackData.name());
        assertTrue(actualGPSTrackData.gpxTime().isPresent());
        assertEquals(metaTime, actualGPSTrackData.gpxTime().get());
        assertEquals(12345.6, actualGPSTrackData.distance().toMeters(), Constant.EPSILON);
        assertEquals(3000.0, actualGPSTrackData.duration().toSeconds(), Constant.EPSILON);
        assertEquals(4.1152, actualGPSTrackData.speed().toMetersPerSecond(), Constant.EPSILON);
        assertEquals(200.0, actualGPSTrackData.elevationGain().toMeters(), Constant.EPSILON);
        assertEquals(2500.0, actualGPSTrackData.motionTime().toSeconds(), Constant.EPSILON);
        assertEquals(500.0, actualGPSTrackData.pausingTime().toSeconds(), Constant.EPSILON);
        assertEquals(2, actualGPSTrackData.kilometerSpeeds().size(), "Expected two kilometer speeds");
        assertEquals(10.0, actualGPSTrackData.kilometerSpeeds().getFirst().toMetersPerSecond(), Constant.EPSILON);
        assertEquals(9.5, actualGPSTrackData.kilometerSpeeds().getLast().toMetersPerSecond(), Constant.EPSILON);

        List<GPSPosition> positions = actualGPSTrackData.gpsPositions();
        assertEquals(2, positions.size());
        GPSPosition position1 = positions.getFirst();
        assertEquals(Instant.parse("2025-08-22T10:00:10Z"), position1.timestamp());
        assertEquals(50.0, position1.latitude());
        assertEquals(5.0, position1.longitude());
        assertNotNull(position1.altitude());
        assertEquals(100.5, position1.altitude());

        GPSPosition position2 = positions.get(1);
        assertEquals(Instant.parse("2025-08-22T10:05:10Z"), position2.timestamp());
        assertEquals(50.001, position2.latitude());
        assertEquals(5.002, position2.longitude());
        assertNull(position2.altitude());
    }

    @Test
    void processGpxFile_withoutMetadataNameOrTime_usesEmptyNameAndFirstWaypointTimeAndMapsMissingFields() throws IOException {
        GpxWaypointProcessor waypointProcessor = Mockito.mock(GpxWaypointProcessor.class);
        GpxMetricsCalculator metricsCalculator = Mockito.mock(GpxMetricsCalculator.class);
        KilometerMetricsCalculator kilometerMetricsCalculator = Mockito.mock(KilometerMetricsCalculator.class);
        SpeedCalculator speedCalculator = Mockito.mock(SpeedCalculator.class);
        GpxService service = new GpxService(waypointProcessor, metricsCalculator, kilometerMetricsCalculator, speedCalculator);
        Path gpxPath = tempDir.resolve("minimal.gpx");
        Files.writeString(gpxPath, minimalGpx());

        Instant firstTime = Instant.parse("2025-08-22T11:00:00Z");
        WayPoint wp1 = WayPoint.builder().lat(0.0).lon(0.0).time(firstTime).build();
        WayPoint wp2 = WayPoint.builder().lat(0.1).lon(0.2).build();
        List<WayPoint> wayPoints = List.of(wp1, wp2);
        when(waypointProcessor.getAllWayPointsOrderedByTime(any())).thenReturn(wayPoints);

        when(metricsCalculator.calculateDistance(wayPoints)).thenReturn(Distance.ofMeters(1000.0));
        when(metricsCalculator.calculateDuration(wayPoints)).thenReturn(Duration.ofSeconds(600L));
        when(speedCalculator.calculateSpeed(Distance.ofMeters(1000.0), Duration.ofSeconds(600L)))
            .thenReturn(Speed.ofMetersPerSecond(1.6667));
        when(metricsCalculator.calculateElevationGain(wayPoints)).thenReturn(Distance.zero());
        when(metricsCalculator.calculateMotionAndPausingTime(wayPoints)).thenReturn(
            MotionAndPausingTime
                .builder()
                .motionTime(Duration.ofSeconds(600L))
                .pausingTime(Duration.zero())
                .build()
        );
        when(kilometerMetricsCalculator.calculateKilometerMetrics(wayPoints)).thenReturn(
            new KilometerMetrics(
                List.of(Speed.ofMetersPerSecond(1.6)),
                List.of(Pace.ofSecondsPerKilometer(37.0)
                )
            )
        );

        GPSTrackData actualGPSTrackData = service.processGpxFile(gpxPath);

        assertInstanceOf(GPSTrackData.class, actualGPSTrackData);
        assertEquals("", actualGPSTrackData.name(), "Expected empty name when no metadata/track name available");
        assertTrue(actualGPSTrackData.gpxTime().isPresent());
        assertEquals(firstTime, actualGPSTrackData.gpxTime().get(), "Expected gpxTime from first waypoint when metadata time missing");
        assertEquals(1000.0, actualGPSTrackData.distance().toMeters(), Constant.EPSILON);
        assertEquals(600.0, actualGPSTrackData.duration().toSeconds(), Constant.EPSILON);
        assertEquals(1.6667, actualGPSTrackData.speed().toMetersPerSecond(), Constant.EPSILON);
        assertEquals(0.0, actualGPSTrackData.elevationGain().toMeters(), Constant.EPSILON);
        assertEquals(600.0, actualGPSTrackData.motionTime().toSeconds(), Constant.EPSILON);
        assertEquals(0L, actualGPSTrackData.pausingTime().toSeconds(), Constant.EPSILON);
        assertEquals(1, actualGPSTrackData.kilometerSpeeds().size(), "Expected one kilometer speed");
        assertEquals(1.6, actualGPSTrackData.kilometerSpeeds().getFirst().toMetersPerSecond(), Constant.EPSILON);

        List<GPSPosition> positions = actualGPSTrackData.gpsPositions();
        assertEquals(2, positions.size());
        GPSPosition position1 = positions.getFirst();
        assertEquals(firstTime, position1.timestamp());
        assertEquals(0.0, position1.latitude());
        assertEquals(0.0, position1.longitude());
        assertNull(position1.altitude(), "No elevation set for wp1 in this test");

        GPSPosition position2 = positions.get(1);
        assertEquals(Instant.MIN, position2.timestamp());
        assertEquals(0.1, position2.latitude());
        assertEquals(0.2, position2.longitude());
        assertNull(position2.altitude());
    }
}
