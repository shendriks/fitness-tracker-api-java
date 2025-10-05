package dev.shendriks.fitnesstrackerapi.adapter.out.jpa;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ChallengeDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ChallengeParticipationDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper.ChallengeParticipationDbEntityMapper;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.ChallengeDbEntityRepository;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.ChallengeParticipationDbEntityRepository;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.UserRepository;
import dev.shendriks.fitnesstrackerapi.application.exception.ChallengeNotFoundException;
import dev.shendriks.fitnesstrackerapi.application.exception.UserNotFoundException;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingChallengeParticipations;
import dev.shendriks.fitnesstrackerapi.domain.entity.ChallengeParticipation;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeParticipationId;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

/**
 * JPA adapter for managing user participations in challenges.
 *
 * <p>Provides queries and commands to join/leave challenges and update progress.</p>
 */
@Repository
@AllArgsConstructor
public class ChallengeParticipationJpaRepositoryAdapter implements ForAccessingChallengeParticipations {
    private final ChallengeParticipationDbEntityRepository repository;
    private final ChallengeDbEntityRepository challengeRepository;
    private final UserRepository userRepository;
    private final ChallengeParticipationDbEntityMapper mapper;
    private final Clock clock;

    /**
     * Retrieves all current challenge participations for the given user.
     *
     * @param userId the user whose participations to fetch
     * @return list of participations
     */
    @Override
    public List<ChallengeParticipation> findCurrentByUser(UserId userId) {
        List<ChallengeParticipationDbEntity> entities = repository.findCurrent(userId.value(), Instant.now(clock));
        return mapper.toChallengeParticipations(entities);
    }

    /**
     * Updates the percentage completed for a participation.
     *
     * @param challengeParticipationId the participation id
     * @param percentageCompleted new completion percentage
     */
    @Override
    public void updatePercentageCompleted(ChallengeParticipationId challengeParticipationId, int percentageCompleted) {
        ChallengeParticipationDbEntity entity = repository.findById(challengeParticipationId.value()).orElseThrow();
        entity.setPercentageCompleted(percentageCompleted);
        repository.save(entity);
    }

    /**
     * Checks if the given user already joined the specified challenge.
     *
     * @param userId the user id
     * @param challengeUlid the challenge ULID
     * @return true if a participation exists
     */
    @Override
    public boolean existsByChallengeAndUser(UserId userId, ChallengeUlid challengeUlid) {
        return repository.existsByChallengeUlidAndUserId(challengeUlid.getValue(), userId.value());
    }

    /**
     * Removes a user's participation from a challenge if present.
     *
     * @param userId the user id
     * @param challengeUlid the challenge ULID
     * @throws UserNotFoundException if the user does not exist
     * @throws ChallengeNotFoundException if the challenge does not exist
     */
    @Override
    public void leaveChallenge(UserId userId, ChallengeUlid challengeUlid) {
        UserDbEntity user = userRepository.findById(userId.value()).orElseThrow(UserNotFoundException::new);
        ChallengeDbEntity challenge = challengeRepository
            .findByUlid(challengeUlid.getValue())
            .orElseThrow(() -> new ChallengeNotFoundException(challengeUlid.getValue()));
        repository.findByChallengeAndUser(challenge, user).ifPresent(repository::delete);
    }

    /**
     * Creates a participation for the given user and challenge.
     *
     * @param userId the user id
     * @param challengeUlid the challenge ULID
     * @return the created participation
     * @throws UserNotFoundException if the user does not exist
     * @throws ChallengeNotFoundException if the challenge does not exist
     */
    @Override
    public ChallengeParticipation joinChallenge(UserId userId, ChallengeUlid challengeUlid) {
        UserDbEntity user = userRepository.findById(userId.value()).orElseThrow(UserNotFoundException::new);
        ChallengeDbEntity challengeDbEntity = challengeRepository
            .findByUlid(challengeUlid.getValue())
            .orElseThrow(() -> new ChallengeNotFoundException(challengeUlid.getValue()));
        ChallengeParticipationDbEntity participationDbEntity = new ChallengeParticipationDbEntity();
        participationDbEntity.setChallenge(challengeDbEntity);
        participationDbEntity.setUser(user);
        participationDbEntity = repository.save(participationDbEntity);
        return mapper.toChallengeParticipation(participationDbEntity);
    }
}
