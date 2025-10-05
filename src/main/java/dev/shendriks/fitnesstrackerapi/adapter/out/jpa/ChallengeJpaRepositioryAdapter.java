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

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * JPA adapter for accessing and querying Challenge projections and entities.
 *
 * <p>Provides lookup and existence operations for challenges, scoped to users when applicable.</p>
 */
@Repository
@AllArgsConstructor
public class ChallengeJpaRepositioryAdapter implements ForAccessingChallenges {
    private final ChallengeDbEntityRepository challengeDbEntityRepository;
    private final ChallengeDbEntityMapper mapper;
    private final Clock clock;

    /**
     * Finds all current challenges for a user, including whether the user has joined them.
     *
     * @param userId the user whose current challenges to fetch
     * @return list of challenges
     */
    @Override
    public List<Challenge> findAllByUser(UserId userId) {
        List<ChallengeProjection> projections = challengeDbEntityRepository
            .findAllCurrentWithUserJoined(userId.value(), Instant.now(clock));
        return mapper.toChallenges(projections);
    }

    /**
     * Checks whether a challenge with the given ULID exists.
     *
     * @param challengeUlid the ULID of the challenge
     * @return true if a challenge exists, false otherwise
     */
    @Override
    public boolean existsByUlid(ChallengeUlid challengeUlid) {
        return challengeDbEntityRepository.existsByUlid(challengeUlid.getValue());
    }

    /**
     * Finds a challenge by user and challenge ULID, including whether the user joined it.
     *
     * @param userId the user context
     * @param challengeUlid challenge identifier
     * @return an optional challenge
     */
    @Override
    public Optional<Challenge> findByUserIdAndUlid(UserId userId, ChallengeUlid challengeUlid) {
        Optional<ChallengeProjection> entity = challengeDbEntityRepository.findByUserIdAndUlidWithUserJoined(
            userId.value(),
            challengeUlid.getValue()
        );
        return entity.map(mapper::toChallenge);
    }

    /**
     * Retrieves the name of a challenge by its id.
     *
     * @param challengeId the database id
     * @return optional name if found
     */
    @Override
    public Optional<String> findNameById(ChallengeId challengeId) {
        return challengeDbEntityRepository.findNameById(challengeId.getValue());
    }

    /**
     * Retrieves the name of a challenge by its ULID.
     *
     * @param challengeUlid the ULID
     * @return optional name if found
     */
    @Override
    public Optional<String> findNameByUlid(ChallengeUlid challengeUlid) {
        return challengeDbEntityRepository.findNameByUlid(challengeUlid.getValue());
    }
}
