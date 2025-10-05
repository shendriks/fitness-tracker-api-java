package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.Builder;

import java.time.Instant;

/**
 * Immutable GPS position including an altitude value.
 *
 * <p>This variant uses non-optional altitude when the value is known for every point.</p>
 */
@Builder
public record GPSPosition(
    Instant timestamp,
    Double latitude,
    Double longitude,
    Double altitude
) {
}
