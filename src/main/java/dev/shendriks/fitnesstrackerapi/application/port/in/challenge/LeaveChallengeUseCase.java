package dev.shendriks.fitnesstrackerapi.application.port.in.challenge;

import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public interface LeaveChallengeUseCase {
    void leaveChallenge(UserId userId, ChallengeUlid challengeId);
}

