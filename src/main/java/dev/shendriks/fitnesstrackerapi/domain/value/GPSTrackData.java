package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Builder
public record GPSTrackData(
    String name,
    Optional<Instant> gpxTime,
    Distance distance,
    Duration duration,
    Speed speed,
    Distance elevationGain,
    Duration motionTime,
    Duration pausingTime,
    List<Speed> kilometerSpeeds,
    List<GPSPosition> gpsPositions
) {
}
