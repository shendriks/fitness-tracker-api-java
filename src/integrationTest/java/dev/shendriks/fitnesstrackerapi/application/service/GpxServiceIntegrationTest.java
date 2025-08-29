package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.domain.value.GPSPositionData;
import dev.shendriks.fitnesstrackerapi.domain.value.GPSTrackData;
import dev.shendriks.fitnesstrackerapi.infrastructure.Constant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GpxServiceIntegrationTest {
    @TempDir
    Path tempDir;

    @Autowired
    private GpxService gpxService;

    private static String gpxWithMetadataAndTwoPoints(String name, String timeIso) {
        return """
            <gpx version="1.1" creator="it" xmlns="http://www.topografix.com/GPX/1/1">
                %s
                <trk>
                    <trkseg>
                        <trkpt lat="0.0" lon="0.0">
                            <time>2025-08-23T10:00:05Z</time>
                            <ele>50.0</ele>
                        </trkpt>
                        <trkpt lat="0.0" lon="0.009">
                            <time>2025-08-23T10:01:45Z</time>
                            <ele>60.0</ele>
                        </trkpt>
                    </trkseg>
                </trk>
            </gpx>
            """
            .formatted("<metadata><name>" + name + "</name><time>" + timeIso + "</time></metadata>");
    }

    private static String gpxWithoutMetadataWithTrackName(String trackName) {
        return """
            <gpx version="1.1" creator="it" xmlns="http://www.topografix.com/GPX/1/1">
                <trk>
                    %s
                    <trkseg>
                        <trkpt lat="52.0" lon="5.0">
                            <time>2025-08-23T19:00:00Z</time>
                        </trkpt>
                        <trkpt lat="52.0005" lon="5.0005">
                            <time>2025-08-23T19:05:00Z</time>
                        </trkpt>
                    </trkseg>
                </trk>
            </gpx>
            """
            .formatted("<name>" + trackName + "</name>");
    }

    @Test
    void processGpxFile_withMetadataNameAndTime_worksAsExpected() throws IOException {
        // Two points near the equator roughly 1000 meters apart in longitude
        // Duration 100s. Expect speed ~= distance/duration, pace ~= 1000/speed.
        String name = "Morning Run";
        Instant metaTime = Instant.parse("2025-08-23T10:00:00Z");
        String gpx = gpxWithMetadataAndTwoPoints(name, metaTime.toString());
        Path file = tempDir.resolve("with_meta_it.gpx");
        Files.writeString(file, gpx);

        GPSTrackData actualGPSTrackData = gpxService.processGpxFile(file);

        assertInstanceOf(GPSTrackData.class, actualGPSTrackData);
        assertEquals(name, actualGPSTrackData.name());
        assertTrue(actualGPSTrackData.gpxTime().isPresent());
        assertEquals(metaTime, actualGPSTrackData.gpxTime().get());

        // Waypoints are both kept (they have time)
        List<GPSPositionData> positions = actualGPSTrackData.gpsPositions();
        assertEquals(2, positions.size());
        assertEquals(Instant.parse("2025-08-23T10:00:05Z"), positions.get(0).timestamp());
        assertEquals(Instant.parse("2025-08-23T10:01:45Z"), positions.get(1).timestamp());
        assertTrue(positions.get(0).altitude().isPresent());
        assertEquals(50.0, positions.get(0).altitude().get());
        assertTrue(positions.get(1).altitude().isPresent());
        assertEquals(60.0, positions.get(1).altitude().get());

        // Basic metric sanity checks with tolerances
        assertTrue(
            actualGPSTrackData.distance().toMeters() > 900
                && actualGPSTrackData.distance().toMeters() < 1100,
            "Expected distance around 1km"
        );
        assertEquals(100.0, actualGPSTrackData.duration().toSeconds(), Constant.EPSILON, "Expected duration 100s");
        assertTrue(
            actualGPSTrackData.speed().toMetersPerSecond() > 9
                && actualGPSTrackData.speed().toMetersPerSecond() < 12,
            "Expected speed around ~10 m/s for ~1km/100s");
        assertTrue(
            actualGPSTrackData.pace().toSecondsPerKilometer() > 80
                && actualGPSTrackData.pace().toSecondsPerKilometer() < 120,
            "Expected pace around ~100 s/km");
        assertTrue(
            actualGPSTrackData.elevationGain().toMeters() >= 10.0 - 0.001
                && actualGPSTrackData.elevationGain().toMeters() <= 10.0 + 0.001,
            "Expected elevation gain 10m");

        // Motion/pausing time depends on speed threshold in calculator; with ~10 m/s it should be all motion
        assertEquals(100.0, actualGPSTrackData.motionTime().toSeconds(), Constant.EPSILON, "Expected motion time 100s");
        assertEquals(0.0, actualGPSTrackData.pausingTime().toSeconds(), Constant.EPSILON, "Expected pausing time 0s");

        // Kilometer metrics: for ~1km segment we expect at least one entry
        assertFalse(actualGPSTrackData.kilometerSpeeds().isEmpty(), "Expected at least one kilometer speed entry");
        assertFalse(actualGPSTrackData.kilometerPaces().isEmpty(), "Expected at least one kilometer pace entry");
    }

    @Test
    void processGpxFile_withoutMetadata_usesTrackNameAndFirstWaypointTime() throws IOException {
        String trackName = "Evening Walk";
        String gpx = gpxWithoutMetadataWithTrackName(trackName);
        Path file = tempDir.resolve("without_meta_it.gpx");
        Files.writeString(file, gpx);

        GPSTrackData actualGPSTrackData = gpxService.processGpxFile(file);

        assertNotNull(actualGPSTrackData);
        assertEquals(trackName, actualGPSTrackData.name());
        assertTrue(actualGPSTrackData.gpxTime().isPresent());
        assertEquals(Instant.parse("2025-08-23T19:00:00Z"), actualGPSTrackData.gpxTime().get());

        // Positions
        List<GPSPositionData> positions = actualGPSTrackData.gpsPositions();
        assertEquals(2, positions.size());
        assertEquals(Instant.parse("2025-08-23T19:00:00Z"), positions.get(0).timestamp());
        assertEquals(Instant.parse("2025-08-23T19:05:00Z"), positions.get(1).timestamp());
        assertTrue(positions.get(0).altitude().isEmpty(), "Expected no altitude for first position");
        assertTrue(positions.get(1).altitude().isEmpty(), "Expected no altitude for second position");

        // Sanity metrics: small distance, 300 seconds duration
        assertTrue(actualGPSTrackData.distance().toMeters() > 0, "Expected distance > 0m");
        assertEquals(300.0, actualGPSTrackData.duration().toSeconds(), Constant.EPSILON, "Expected duration 300s");
        assertTrue(actualGPSTrackData.speed().toMetersPerSecond() >= 0, "Expected speed >= 0m/s");
        assertTrue(actualGPSTrackData.pace().toSecondsPerKilometer() >= 0, "Expected pace >= 0s/km");
        // No elevation tags -> elevation gain should be 0
        assertEquals(0.0, actualGPSTrackData.elevationGain().toMeters(), Constant.EPSILON, "Expected elevation gain 0m");
        // With small movement but above threshold between timed points, should count as motion
        assertEquals(
            300.0,
            actualGPSTrackData.motionTime().toSeconds() + actualGPSTrackData.pausingTime().toSeconds(),
            Constant.EPSILON,
            "Expected motion time + pausing time == duration"
        );
    }
}
