package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ChallengeParticipationDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.ChallengeParticipation;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", imports = {UserId.class})
public abstract class ChallengeParticipationDbEntityMapper {
    @Mapping(target = "challenge.hasUserJoined", constant = "true")
    @Mapping(target = "userId", expression = "java(new UserId(entity.getUser().getId()))")
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
