package dev.shendriks.fitnesstrackerapi.application.event;

import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public record ChallengeCompletedEvent(UserId userId, ChallengeId challengeId) {
}
