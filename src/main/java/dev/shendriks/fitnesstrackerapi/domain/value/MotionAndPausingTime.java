package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.Builder;

@Builder
public record MotionAndPausingTime(
    Duration motionTime,
    Duration pausingTime
) {
}
