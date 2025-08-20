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
    @Mapping(target = "becameComplete", ignore = true)
    @Mapping(target = "becameIncomplete", ignore = true)
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

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "ulid", ignore = true)
//    @Mapping(target = "challenge.id", expression = "java(challengeParticipation.getChallenge().getId().id())")
//    @Mapping(target = "challenge.ulid", expression = "java(challengeParticipation.getChallenge().getUlid().ulid())")
//    @Mapping(target = "user", expression = "java(challengeParticipation.getUser())")
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
//    @Mapping(target = "challenge.createdAt", ignore = true)
//    @Mapping(target = "challenge.updatedAt", ignore = true)
//    @Mapping(target = "challenge.challengeParticipations", ignore = true)
//    public ChallengeParticipationDbEntity toNewChallengeDbEntity(ChallengeParticipation challengeParticipation) {
//        var entity = new ChallengeParticipationDbEntity();
//        entity.setPercentageCompleted(challengeParticipation.getPercentageCompleted());
//        
//        return entity;
//    }
}
