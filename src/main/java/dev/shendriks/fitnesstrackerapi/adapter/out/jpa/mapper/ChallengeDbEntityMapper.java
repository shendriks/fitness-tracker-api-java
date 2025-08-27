package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection.ChallengeProjection;
import dev.shendriks.fitnesstrackerapi.domain.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeId;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeUlid;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ChallengeDbEntityMapper {
    public abstract Challenge toChallenge(ChallengeProjection projection);

    public abstract List<Challenge> toChallenges(List<ChallengeProjection> projections);

    ChallengeId mapChallengeId(Long id) {
        return new ChallengeId(id);
    }

    ChallengeUlid mapChallengeUlid(String ulid) {
        return new ChallengeUlid(ulid);
    }
}
