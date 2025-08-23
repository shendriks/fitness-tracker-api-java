package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.service.gpx.GpxMetricsCalculator;
import dev.shendriks.fitnesstrackerapi.application.service.gpx.GpxWaypointProcessor;
import dev.shendriks.fitnesstrackerapi.application.service.gpx.KilometerMetricsCalculator;
import dev.shendriks.fitnesstrackerapi.domain.value.GPSPositionData;
import dev.shendriks.fitnesstrackerapi.domain.value.GPSTrackData;
import dev.shendriks.fitnesstrackerapi.domain.value.KilometerMetrics;
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
    void processGpxFile_withMetadataNameAndTime_mapsAllFieldsFromCalculators_andMetadataNameTime() throws IOException {
        GpxWaypointProcessor waypointProcessor = Mockito.mock(GpxWaypointProcessor.class);
        GpxMetricsCalculator metricsCalculator = Mockito.mock(GpxMetricsCalculator.class);
        KilometerMetricsCalculator kilometerMetricsCalculator = Mockito.mock(KilometerMetricsCalculator.class);
        GpxService service = new GpxService(waypointProcessor, metricsCalculator, kilometerMetricsCalculator);

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

        when(metricsCalculator.calculateTotalLength(wayPoints)).thenReturn(12345.6);
        when(metricsCalculator.calculateDuration(wayPoints)).thenReturn(3000L);
        when(metricsCalculator.calculateSpeed(12345.6, 3000L)).thenReturn(4.1152);
        when(metricsCalculator.calculatePace(4.1152)).thenReturn(14.6);
        when(metricsCalculator.calculateElevationGain(wayPoints)).thenReturn(200.0);
        when(metricsCalculator.calculateMotionAndPausingTime(wayPoints)).thenReturn(new long[]{2500L, 500L});
        when(kilometerMetricsCalculator.calculateKilometerMetrics(wayPoints)).thenReturn(
            new KilometerMetrics(List.of(10.0, 9.5), List.of(6.0, 6.3))
        );

        GPSTrackData actualGPSTrackData = service.processGpxFile(gpxPath);

        assertInstanceOf(GPSTrackData.class, actualGPSTrackData);
        assertEquals(name, actualGPSTrackData.name());
        assertTrue(actualGPSTrackData.gpxTime().isPresent());
        assertEquals(metaTime, actualGPSTrackData.gpxTime().get());
        assertEquals(12345.6, actualGPSTrackData.totalLength());
        assertEquals(3000L, actualGPSTrackData.duration());
        assertEquals(4.1152, actualGPSTrackData.speed());
        assertEquals(14.6, actualGPSTrackData.pace());
        assertEquals(200.0, actualGPSTrackData.elevationGain());
        assertEquals(2500L, actualGPSTrackData.motionTime());
        assertEquals(500L, actualGPSTrackData.pausingTime());
        assertEquals(List.of(10.0, 9.5), actualGPSTrackData.kilometerSpeeds());
        assertEquals(List.of(6.0, 6.3), actualGPSTrackData.kilometerPaces());

        List<GPSPositionData> positions = actualGPSTrackData.gpsPositions();
        assertEquals(2, positions.size());
        GPSPositionData position1 = positions.getFirst();
        assertEquals(Instant.parse("2025-08-22T10:00:10Z"), position1.timestamp());
        assertEquals(50.0, position1.latitude());
        assertEquals(5.0, position1.longitude());
        assertTrue(position1.altitude().isPresent());
        assertEquals(100.5, position1.altitude().get());

        GPSPositionData position2 = positions.get(1);
        assertEquals(Instant.parse("2025-08-22T10:05:10Z"), position2.timestamp());
        assertEquals(50.001, position2.latitude());
        assertEquals(5.002, position2.longitude());
        assertTrue(position2.altitude().isEmpty());
    }

    @Test
    void processGpxFile_withoutMetadataNameOrTime_usesEmptyName_andFirstWaypointTime_andMapsMissingFields() throws IOException {
        GpxWaypointProcessor waypointProcessor = Mockito.mock(GpxWaypointProcessor.class);
        GpxMetricsCalculator metricsCalculator = Mockito.mock(GpxMetricsCalculator.class);
        KilometerMetricsCalculator kilometerMetricsCalculator = Mockito.mock(KilometerMetricsCalculator.class);
        GpxService service = new GpxService(waypointProcessor, metricsCalculator, kilometerMetricsCalculator);
        Path gpxPath = tempDir.resolve("minimal.gpx");
        Files.writeString(gpxPath, minimalGpx());

        Instant firstTime = Instant.parse("2025-08-22T11:00:00Z");
        WayPoint wp1 = WayPoint.builder().lat(0.0).lon(0.0).time(firstTime).build();
        WayPoint wp2 = WayPoint.builder().lat(0.1).lon(0.2).build();
        List<WayPoint> wayPoints = List.of(wp1, wp2);
        when(waypointProcessor.getAllWayPointsOrderedByTime(any())).thenReturn(wayPoints);

        when(metricsCalculator.calculateTotalLength(wayPoints)).thenReturn(1000.0);
        when(metricsCalculator.calculateDuration(wayPoints)).thenReturn(600L);
        when(metricsCalculator.calculateSpeed(1000.0, 600L)).thenReturn(1.6667);
        when(metricsCalculator.calculatePace(1.6667)).thenReturn(36.0);
        when(metricsCalculator.calculateElevationGain(wayPoints)).thenReturn(0.0);
        when(metricsCalculator.calculateMotionAndPausingTime(wayPoints)).thenReturn(new long[]{600L, 0L});
        when(kilometerMetricsCalculator.calculateKilometerMetrics(wayPoints)).thenReturn(
            new KilometerMetrics(List.of(1.6), List.of(37.0))
        );

        GPSTrackData actualGPSTrackData = service.processGpxFile(gpxPath);

        assertInstanceOf(GPSTrackData.class, actualGPSTrackData);
        assertEquals("", actualGPSTrackData.name(), "Expected empty name when no metadata/track name available");
        assertTrue(actualGPSTrackData.gpxTime().isPresent());
        assertEquals(firstTime, actualGPSTrackData.gpxTime().get(), "Expected gpxTime from first waypoint when metadata time missing");
        assertEquals(1000.0, actualGPSTrackData.totalLength());
        assertEquals(600L, actualGPSTrackData.duration());
        assertEquals(1.6667, actualGPSTrackData.speed());
        assertEquals(36.0, actualGPSTrackData.pace());
        assertEquals(0.0, actualGPSTrackData.elevationGain());
        assertEquals(600L, actualGPSTrackData.motionTime());
        assertEquals(0L, actualGPSTrackData.pausingTime());
        assertEquals(List.of(1.6), actualGPSTrackData.kilometerSpeeds());
        assertEquals(List.of(37.0), actualGPSTrackData.kilometerPaces());

        List<GPSPositionData> positions = actualGPSTrackData.gpsPositions();
        assertEquals(2, positions.size());
        GPSPositionData position1 = positions.getFirst();
        assertEquals(firstTime, position1.timestamp());
        assertEquals(0.0, position1.latitude());
        assertEquals(0.0, position1.longitude());
        assertTrue(position1.altitude().isEmpty(), "No elevation set for wp1 in this test");

        GPSPositionData position2 = positions.get(1);
        assertEquals(Instant.MIN, position2.timestamp());
        assertEquals(0.1, position2.latitude());
        assertEquals(0.2, position2.longitude());
        assertTrue(position2.altitude().isEmpty());
    }
}
