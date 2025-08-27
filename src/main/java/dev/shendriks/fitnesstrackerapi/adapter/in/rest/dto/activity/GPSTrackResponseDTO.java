package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public record GPSTrackResponseDTO(
    String name,
    Optional<Instant> gpxTime,
    double totalLength,
    long duration,
    double speed,
    double pace,
    double elevationGain,
    long motionTime,
    long pausingTime,
    List<Double> kilometerSpeeds,
    List<Double> kilometerPaces,
    List<GPSPositionResponseDTO> gpsPositions
) {
}