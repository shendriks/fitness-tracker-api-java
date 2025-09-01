package dev.shendriks.fitnesstrackerapi.domain.value;

import java.util.List;

public record KilometerMetrics(
    List<Speed> speeds,
    List<Pace> paces
) {
}