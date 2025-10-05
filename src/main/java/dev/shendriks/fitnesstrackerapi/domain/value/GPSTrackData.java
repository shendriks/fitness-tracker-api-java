package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Aggregated GPS track metrics and data points derived from a GPX file or similar source.
 *
 * <p>Includes overall metrics (distance, duration, elevation gain, average speed),
 * split metrics (e.g., kilometer speeds), and the list of recorded GPS positions.</p>
 */
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
    List<GPSPositionData> gpsPositions
) {
}
