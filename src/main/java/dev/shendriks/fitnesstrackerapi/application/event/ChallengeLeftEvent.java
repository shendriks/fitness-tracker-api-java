package dev.shendriks.fitnesstrackerapi.application.event;

import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public record ChallengeLeftEvent(UserId userId, ChallengeUlid challengeUlid) {
}
