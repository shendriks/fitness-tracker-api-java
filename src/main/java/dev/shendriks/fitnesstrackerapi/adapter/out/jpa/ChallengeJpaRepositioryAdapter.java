package dev.shendriks.fitnesstrackerapi.adapter.out.jpa;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper.ChallengeDbEntityMapper;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection.ChallengeProjection;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.ChallengeDbEntityRepository;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingChallenges;
import dev.shendriks.fitnesstrackerapi.domain.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeId;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class ChallengeJpaRepositioryAdapter implements ForAccessingChallenges {
    private final ChallengeDbEntityRepository challengeDbEntityRepository;
    private final ChallengeDbEntityMapper mapper;

    @Override
    public List<Challenge> findAllByUser(UserId userId) {
        List<ChallengeProjection> projections = challengeDbEntityRepository
            .findAllCurrentWithUserJoined(userId.value(), Instant.now());
        return mapper.toChallenges(projections);
    }

    @Override
    public Optional<Challenge> findByUlid(ChallengeUlid challengeUlid) {
        Optional<ChallengeProjection> entity = challengeDbEntityRepository.findWithUserJoinedByUlid(challengeUlid.getValue());
        return entity.map(mapper::toChallenge);
    }

    @Override
    public Optional<String> findNameById(ChallengeId challengeId) {
        return challengeDbEntityRepository.findNameById(challengeId.getValue());
    }

    @Override
    public Optional<String> findNameByUlid(ChallengeUlid challengeUlid) {
        return challengeDbEntityRepository.findNameByUlid(challengeUlid.getValue());
    }
}
