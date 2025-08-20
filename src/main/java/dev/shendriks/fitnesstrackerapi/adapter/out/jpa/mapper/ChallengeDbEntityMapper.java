package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ChallengeDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection.ChallengeProjection;
import dev.shendriks.fitnesstrackerapi.domain.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeId;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeUlid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ChallengeDbEntityMapper {
    @Mapping(target = "hasUserJoined", source = "hasUserJoined")
    public abstract Challenge toChallenge(ChallengeProjection projection);

    public abstract List<Challenge> toChallenges(List<ChallengeProjection> projections);

    @Mapping(target = "hasUserJoined", ignore = true)
    public abstract Challenge toChallenge(ChallengeDbEntity entity);

    ChallengeId mapChallengeId(Long id) {
        return new ChallengeId(id);
    }

    ChallengeUlid mapChallengeUlid(String ulid) {
        return new ChallengeUlid(ulid);
    }
}
