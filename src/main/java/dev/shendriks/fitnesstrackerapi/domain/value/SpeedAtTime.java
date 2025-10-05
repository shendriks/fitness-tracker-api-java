package dev.shendriks.fitnesstrackerapi.domain.value;

import java.time.Instant;

public record SpeedAtTime(
    Instant timestamp,
    Speed speed
) {
}
