package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;

import java.time.Instant;

public record ActivityResponseDTO(
    String id,
    ActivityType activityType,
    Long duration,
    Double distance,
    Double averageSpeed,
    Instant createdAt,
    Instant updatedAt,
    String title,
    String description,
    Instant startDate
) {
}
