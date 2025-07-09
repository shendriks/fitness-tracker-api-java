package dev.shendriks.fitnesstrackerapi.domain.activity.dto;

import java.time.Instant;

public record GPSPositionResponse(
    Instant timestamp,
    Double latitude,
    Double longitude
) {
}
