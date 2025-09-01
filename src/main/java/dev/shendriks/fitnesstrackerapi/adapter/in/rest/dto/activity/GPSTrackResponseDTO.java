package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public record GPSTrackResponseDTO(
    String name,
    Optional<Instant> gpxTime,
    Long distance,
    Double duration,
    Double speed,
    Double pace,
    Long elevationGain,
    Double motionTime,
    Double pausingTime,
    List<Double> kilometerSpeeds,
    List<Double> kilometerPaces,
    List<GPSPositionResponseDTO> gpsPositions
) {
}