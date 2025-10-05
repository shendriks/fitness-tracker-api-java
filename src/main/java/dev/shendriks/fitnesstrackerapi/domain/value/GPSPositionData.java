package dev.shendriks.fitnesstrackerapi.domain.value;

import java.time.Instant;
import java.util.Optional;

/**
 * Immutable data record representing a single GPS waypoint.
 *
 * <p>Fields:</p>
 * - timestamp: time at which the position was recorded (UTC).
 * - latitude: latitude in decimal degrees.
 * - longitude: longitude in decimal degrees.
 * - altitude: optional altitude in meters above sea level.
 */
public record GPSPositionData(
    Instant timestamp,
    Double latitude,
    Double longitude,
    Optional<Double> altitude
) {
}
