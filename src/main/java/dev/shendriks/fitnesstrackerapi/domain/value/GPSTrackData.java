package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Builder
public record GPSTrackData(
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
    List<GPSPositionData> gpsPositions
) {
}