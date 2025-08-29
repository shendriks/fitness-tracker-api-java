package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;

import java.time.Instant;

public record ActivityResponseDTO(
    String id,
    ActivityType activityType,
    Double duration,
    Double distance,
    int calories,
    Instant createdAt,
    Instant updatedAt,
    String title,
    String description,
    Instant startDate
) {
}
