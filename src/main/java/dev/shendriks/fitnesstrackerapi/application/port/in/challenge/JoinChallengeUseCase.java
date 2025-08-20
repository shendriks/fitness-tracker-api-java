package dev.shendriks.fitnesstrackerapi.application.port.in.challenge;

import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public interface JoinChallengeUseCase {
    void joinChallenge(UserId userId, ChallengeUlid challengeId);
}
