package dev.shendriks.fitnesstrackerapi.domain.value;

public record AchievementCompletionResult(
    int percentageCompleted,
    boolean becameComplete,
    boolean becameIncomplete
 ) {
 }
