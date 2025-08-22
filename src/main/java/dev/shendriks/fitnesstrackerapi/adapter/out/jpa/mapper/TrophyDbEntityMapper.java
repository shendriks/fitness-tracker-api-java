package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ChallengeDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.MilestoneDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.TrophyDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.Trophy;
import dev.shendriks.fitnesstrackerapi.domain.enums.AchievementType;
import dev.shendriks.fitnesstrackerapi.domain.value.TrophyId;
import dev.shendriks.fitnesstrackerapi.domain.value.TrophyUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", uses = {AchievementDbEntityMapper.class})
public abstract class TrophyDbEntityMapper {
    public abstract List<Trophy> toTrophies(List<TrophyDbEntity> entities);

    @Mapping(target = "unlockedAt", source = "createdAt")
    @Mapping(target = "achievementType", expression = "java(mapAchievementType(entity))")
    @Mapping(target = "userId", expression = "java(mapUserId(entity.getUser()))")
    public abstract Trophy toTrophy(TrophyDbEntity entity);

    TrophyId mapTrophyId(Long id) {
        return new TrophyId(id);
    }

    TrophyUlid mapTrophyUlid(String ulid) {
        return new TrophyUlid(ulid);
    }

    UserId mapUserId(UserDbEntity user) {
        return new UserId(user.getId());
    }

    AchievementType mapAchievementType(TrophyDbEntity trophy) {
        return switch (trophy.getAchievement()) {
            case MilestoneDbEntity ignored -> AchievementType.MILESTONE;
            case ChallengeDbEntity ignored -> AchievementType.CHALLENGE;
            case null, default ->
                throw new IllegalArgumentException("Unkown achievement type: %s".formatted(Objects.requireNonNull(trophy.getAchievement()).getClass().getSimpleName()));
        };
    }
}
