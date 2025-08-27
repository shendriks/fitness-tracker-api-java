package dev.shendriks.fitnesstrackerapi.application.event;

import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public record ChallengeBecameIncompleteEvent(UserId userId, ChallengeId challengeId) {
}
