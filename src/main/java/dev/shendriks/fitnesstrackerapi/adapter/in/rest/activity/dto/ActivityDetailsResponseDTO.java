package dev.shendriks.fitnesstrackerapi.adapter.in.rest.activity.dto;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;

import java.time.Instant;
import java.util.List;

public record ActivityDetailsResponseDTO(
    String id,
    ActivityType activityType,
    int duration,
    int calories,
    Instant createdAt,
    Instant updatedAt,
    String title,
    String description,
    int distance,
    Instant startDate,
    List<GPSPositionResponseDTO> gpsPositions
) {
}
