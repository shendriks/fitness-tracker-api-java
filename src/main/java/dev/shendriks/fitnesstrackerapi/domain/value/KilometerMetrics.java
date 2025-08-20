package dev.shendriks.fitnesstrackerapi.domain.value;

import java.util.List;

public record KilometerMetrics(
    List<Double> speeds,
    List<Double> paces
) {
}