package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.Builder;

@Builder
public record MotionAndPausingTime(
    long motionTime,
    long pausingTime
) {
}
