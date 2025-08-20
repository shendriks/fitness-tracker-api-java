package dev.shendriks.fitnesstrackerapi.domain.value;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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