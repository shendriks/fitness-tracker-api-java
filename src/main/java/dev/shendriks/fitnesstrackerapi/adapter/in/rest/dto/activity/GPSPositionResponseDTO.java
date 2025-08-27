package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity;

import java.time.Instant;

public record GPSPositionResponseDTO(
    Instant timestamp,
    Double latitude,
    Double longitude
) {
}
