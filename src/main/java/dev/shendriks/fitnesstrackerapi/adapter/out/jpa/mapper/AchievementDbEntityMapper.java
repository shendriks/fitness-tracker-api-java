package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.AchievementDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.Achievement;
import dev.shendriks.fitnesstrackerapi.domain.value.AchievementId;
import dev.shendriks.fitnesstrackerapi.domain.value.AchievementUlid;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class AchievementDbEntityMapper {
    public abstract Achievement toAchievement(AchievementDbEntity entity);

    AchievementId mapAchievementId(Long id) {
        return new AchievementId(id);
    }

    AchievementUlid mapAchievementUlid(String ulid) {
        return new AchievementUlid(ulid);
    }
}
