package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ChallengeParticipationDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.ChallengeParticipation;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeId;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeParticipationId;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeParticipationUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeUlid;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ChallengeParticipationDbEntityMapper {
    public abstract ChallengeParticipation toChallengeParticipation(ChallengeParticipationDbEntity entity);

    public abstract List<ChallengeParticipation> toChallengeParticipations(List<ChallengeParticipationDbEntity> entities);

    ChallengeId mapChallengeId(Long id) {
        return new ChallengeId(id);
    }

    ChallengeUlid mapChallengeUlid(String ulid) {
        return new ChallengeUlid(ulid);
    }

    ChallengeParticipationId mapChallengeParticipationId(Long id) {
        return new ChallengeParticipationId(id);
    }

    ChallengeParticipationUlid mapChallengeParticipationUlid(String ulid) {
        return new ChallengeParticipationUlid(ulid);
    }
}
