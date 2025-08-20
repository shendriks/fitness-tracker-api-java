package dev.shendriks.fitnesstrackerapi.adapter.in.rest.activity.dto;

import java.time.Instant;

public record GPSPositionResponseDTO(
    Instant timestamp,
    Double latitude,
    Double longitude
) {
}
