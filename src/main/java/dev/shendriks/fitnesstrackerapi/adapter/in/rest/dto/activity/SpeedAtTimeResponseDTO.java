package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity;

import java.time.Instant;

public record SpeedAtTimeResponseDTO(
    Instant timestamp,
    Double speed
) {
}
