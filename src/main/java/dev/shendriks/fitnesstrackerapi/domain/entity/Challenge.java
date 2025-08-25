package dev.shendriks.fitnesstrackerapi.domain.entity;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeId;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeUlid;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@SuperBuilder
public final class Challenge extends Achievement {
    private final Instant startDate;
    private final Instant endDate;
    private final boolean hasUserJoined;

    public Challenge(
        ChallengeId id,
        ChallengeUlid ulid,
        String name,
        String description,
        String imageFilePath,
        ActivityType activityType,
        ActivityMetric activityMetric,
        Long completionThreshold,
        Instant startDate,
        Instant endDate,
        boolean hasUserJoined
    ) {
        super(id, ulid, name, description, imageFilePath, activityType, activityMetric, completionThreshold);
        this.startDate = startDate;
        this.endDate = endDate;
        this.hasUserJoined = hasUserJoined;
    }

    @Override
    public ChallengeId getId() {
        return (ChallengeId) super.getId();
    }

    @Override
    public ChallengeUlid getUlid() {
        return (ChallengeUlid) super.getUlid();
    }
}
