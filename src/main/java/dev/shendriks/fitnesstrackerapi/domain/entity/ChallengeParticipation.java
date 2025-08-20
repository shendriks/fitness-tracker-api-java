package dev.shendriks.fitnesstrackerapi.domain.entity;

import dev.shendriks.fitnesstrackerapi.domain.value.ActivityAggregationMap;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeParticipationId;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeParticipationUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Getter
public final class ChallengeParticipation {
    private final ChallengeParticipationId id;
    private final ChallengeParticipationUlid ulid;
    private final UserId userId;
    private final Challenge challenge;
    private final Instant createdAt;
    private final Instant updatedAt;
    private int percentageCompleted;
    private boolean becameComplete;
    private boolean becameIncomplete;

    public static ChallengeParticipation createNew(UserId userId, Challenge challenge) {
        return new ChallengeParticipation(
            null,
            null,
            userId,
            challenge,
            null,
            null,
            0,
            false,
            false
        );
    }

    public boolean isCompleted() {
        return percentageCompleted >= 100;
    }

    public void updateCompletionPercentage(ActivityAggregationMap activityAggregationMap) {
        var activityStats = challenge.getActivityType() == null
            ? activityAggregationMap.getTotal()
            : activityAggregationMap.getByType(challenge.getActivityType());

        long value = switch (challenge.getActivityMetric()) {
            case ACTIVITY_COUNT -> activityStats.count();
            case LONGEST_SINGLE_DURATION -> activityStats.maxDuration();
            case LONGEST_SINGLE_DISTANCE -> activityStats.maxDistance();
            case TOTAL_DURATION -> activityStats.totalDuration();
            case TOTAL_DISTANCE -> activityStats.totalDistance();
        };

        boolean wasCompleted = isCompleted();
        this.percentageCompleted = Math.min(100, (int) ((double) (value * 100) / (double) challenge.getCompletionThreshold()));

        becameComplete = !wasCompleted && isCompleted();
        becameIncomplete = wasCompleted && !isCompleted();
    }
}


