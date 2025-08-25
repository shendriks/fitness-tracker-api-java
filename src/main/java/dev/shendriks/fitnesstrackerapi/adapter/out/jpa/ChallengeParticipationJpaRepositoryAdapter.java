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

import java.time.Instant;
import java.util.List;

@Repository
@AllArgsConstructor
public class ChallengeParticipationJpaRepositoryAdapter implements ForAccessingChallengeParticipations {
    private final ChallengeParticipationDbEntityRepository repository;
    private final ChallengeDbEntityRepository challengeRepository;
    private final UserRepository userRepository;
    private final ChallengeParticipationDbEntityMapper mapper;

    @Override
    public List<ChallengeParticipation> findCurrentByUser(UserId userId) {
        List<ChallengeParticipationDbEntity> entities = repository.findCurrent(userId.value(), Instant.now());
        return mapper.toChallengeParticipations(entities);
    }

    @Override
    public void updatePercentageCompleted(ChallengeParticipationId challengeParticipationId, int percentageCompleted) {
        ChallengeParticipationDbEntity entity = repository.findById(challengeParticipationId.value()).orElseThrow();
        entity.setPercentageCompleted(percentageCompleted);
        repository.save(entity);
    }

    @Override
    public boolean existsByChallengeAndUser(UserId userId, ChallengeUlid challengeUlid) {
        return repository.existsByChallengeUlidAndUserId(challengeUlid.getValue(), userId.value());
    }

    @Override
    public void leaveChallenge(UserId userId, ChallengeUlid challengeUlid) {
        UserDbEntity user = userRepository.findById(userId.value()).orElseThrow(UserNotFoundException::new);
        ChallengeDbEntity challenge = challengeRepository
            .findByUlid(challengeUlid.getValue())
            .orElseThrow(() -> new ChallengeNotFoundException(challengeUlid.getValue()));
        repository.findByChallengeAndUser(challenge, user).ifPresent(repository::delete);
    }

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
