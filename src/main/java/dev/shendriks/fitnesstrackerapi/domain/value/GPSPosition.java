package dev.shendriks.fitnesstrackerapi.domain.value;

import java.time.Instant;

public record GPSPosition(
    Instant timestamp,
    Double latitude,
    Double longitude,
    Double altitude,
    Double accuracy
) {
}
