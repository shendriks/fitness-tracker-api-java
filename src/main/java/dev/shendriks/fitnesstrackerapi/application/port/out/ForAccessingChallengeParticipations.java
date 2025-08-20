package dev.shendriks.fitnesstrackerapi.application.port.out;

import dev.shendriks.fitnesstrackerapi.domain.entity.ChallengeParticipation;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeParticipationId;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

import java.util.List;
import java.util.Optional;

public interface ForAccessingChallengeParticipations {
    List<ChallengeParticipation> findCurrentByUser(UserId userId);

    void updatePercentageCompleted(ChallengeParticipationId challengeParticipationId, int percentageCompleted);

    boolean existsByChallengeAndUser(UserId userId, ChallengeUlid challengeUlid);

    void leaveChallenge(UserId userId, ChallengeUlid challengeId);

    void create(ChallengeParticipation challengeParticipation);

    Optional<ChallengeParticipation> findByUserAndChallenge(UserId userId, ChallengeUlid challengeUlid);
}
