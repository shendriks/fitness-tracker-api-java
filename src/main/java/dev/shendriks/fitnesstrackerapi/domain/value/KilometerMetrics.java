package dev.shendriks.fitnesstrackerapi.domain.value;

import java.util.List;

/**
 * Per-kilometer derived metrics such as speeds and paces for each split.
 */
public record KilometerMetrics(
    List<Speed> speeds,
    List<Pace> paces
) {
}