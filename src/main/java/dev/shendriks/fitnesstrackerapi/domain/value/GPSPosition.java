package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.Builder;

import java.time.Instant;

@Builder
public record GPSPosition(
    Instant timestamp,
    Double latitude,
    Double longitude,
    Double altitude,
    Double accuracy
) {
}
