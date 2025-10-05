package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;

import java.time.Instant;
import java.util.List;

public record ActivityDetailsResponseDTO(
    String id,
    ActivityType activityType,
    Long duration,
    Double distance,
    Double averageSpeed,
    Instant createdAt,
    Instant updatedAt,
    String title,
    String description,
    Instant startDate,
    List<GPSPositionResponseDTO> gpsPositions,
    List<Double> kilometerSpeeds,
    List<SpeedAtTimeResponseDTO> speeds,
    Double elevationGain,
    Long motionTime,
    Long pausingTime
) {
}
