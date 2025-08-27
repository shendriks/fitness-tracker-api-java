package dev.shendriks.fitnesstrackerapi.application.event;

import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public record ChallengeJoinedEvent(UserId userId, ChallengeUlid challengeUlid) {
}
