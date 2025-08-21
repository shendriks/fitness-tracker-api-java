package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ChallengeParticipationDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.ChallengeParticipation;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeId;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeParticipationId;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeParticipationUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeUlid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ChallengeParticipationDbEntityMapper {
    @Mapping(target = "userId", expression = "java(new UserId(entity.getUser().getId()))")
    @Mapping(target = "challenge.hasUserJoined", constant = "true")
    public abstract ChallengeParticipation toChallengeParticipation(ChallengeParticipationDbEntity entity);

    public abstract List<ChallengeParticipation> toChallengeParticipations(List<ChallengeParticipationDbEntity> entities);

    ChallengeId mapChallengeId(Long id) {
        return new ChallengeId(id);
    }

    ChallengeUlid mapChallengeUlid(String ulid) {
        return new ChallengeUlid(ulid);
    }

    ChallengeParticipationId mapChallengeParticipationsId(Long id) {
        return new ChallengeParticipationId(id);
    }

    ChallengeParticipationUlid mapChallengeParticipationsUlid(String ulid) {
        return new ChallengeParticipationUlid(ulid);
    }
}
