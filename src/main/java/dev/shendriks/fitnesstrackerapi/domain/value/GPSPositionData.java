package dev.shendriks.fitnesstrackerapi.domain.value;

import java.time.Instant;
import java.util.Optional;

public record GPSPositionData(
    Instant timestamp,
    Double latitude,
    Double longitude,
    Optional<Double> altitude
) {
}
