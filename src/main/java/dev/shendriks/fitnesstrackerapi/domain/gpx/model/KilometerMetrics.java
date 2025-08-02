package dev.shendriks.fitnesstrackerapi.domain.gpx.model;

import java.util.List;

public record KilometerMetrics(
    List<Double> speeds,
    List<Double> paces
) {
}