package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.Builder;

@Builder
public record AchievementCompletionResult(
    int percentageCompleted,
    boolean becameComplete,
    boolean becameIncomplete
) {
}
