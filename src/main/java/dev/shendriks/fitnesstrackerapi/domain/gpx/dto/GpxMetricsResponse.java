package dev.shendriks.fitnesstrackerapi.domain.gpx.dto;

import dev.shendriks.fitnesstrackerapi.domain.activity.dto.GPSPositionResponse;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public record GpxMetricsResponse(
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
    List<GPSPositionResponse> gpsPositions
) {
}