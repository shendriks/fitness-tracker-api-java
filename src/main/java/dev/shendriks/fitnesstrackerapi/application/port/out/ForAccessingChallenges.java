package dev.shendriks.fitnesstrackerapi.application.port.out;

import dev.shendriks.fitnesstrackerapi.domain.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeId;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

import java.util.List;
import java.util.Optional;

public interface ForAccessingChallenges {
    List<Challenge> findAllByUser(UserId userId);

    Optional<Challenge> findByUlid(ChallengeUlid challengeUlid);

    Optional<String> findNameById(ChallengeId challengeId);

    Optional<String> findNameByUlid(ChallengeUlid challengeUlid);
}
