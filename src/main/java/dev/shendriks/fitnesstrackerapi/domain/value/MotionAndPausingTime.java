package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.Builder;

/**
 * Pair of motion and pausing durations derived from activity analysis.
 */
@Builder
public record MotionAndPausingTime(
    Duration motionTime,
    Duration pausingTime
) {
}
