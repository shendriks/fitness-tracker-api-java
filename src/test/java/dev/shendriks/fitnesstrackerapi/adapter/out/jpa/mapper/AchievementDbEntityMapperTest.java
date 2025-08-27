package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.AchievementDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.Achievement;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.AchievementId;
import dev.shendriks.fitnesstrackerapi.domain.value.AchievementUlid;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class AchievementDbEntityMapperTest {
    private final AchievementDbEntityMapper mapper = Mappers.getMapper(AchievementDbEntityMapper.class);

    @Test
    void toAchievement_mapsAllFieldsAndWrapsIdTypes() {
        AchievementDbEntity entity = AchievementDbEntity
            .builder()
            .id(42L)
            .ulid("TESTULID000000000000000001")
            .name("Run 100km")
            .description("Complete 100km total distance")
            .imageFilePath("/images/achievements/run-100.png")
            .activityType(ActivityType.RUNNING)
            .activityMetric(ActivityMetric.TOTAL_DISTANCE)
            .completionThreshold(100_000L)
            .build();

        Achievement achievement = mapper.toAchievement(entity);

        assertNotNull(achievement);
        AchievementId achievementId = achievement.getId();
        AchievementUlid achievementUlid = achievement.getUlid();
        assertNotNull(achievementId);
        assertNotNull(achievementUlid);
        assertEquals(42L, achievementId.getValue());
        assertEquals("TESTULID000000000000000001", achievementUlid.getValue());
        assertEquals("Run 100km", achievement.getName());
        assertEquals("Complete 100km total distance", achievement.getDescription());
        assertEquals("/images/achievements/run-100.png", achievement.getImageFilePath());
        assertEquals(ActivityType.RUNNING, achievement.getActivityType());
        assertEquals(ActivityMetric.TOTAL_DISTANCE, achievement.getActivityMetric());
        assertEquals(100_000L, achievement.getCompletionThreshold());
    }

    @Test
    void toAchievement_withNullInput_returnsNull() {
        assertNull(mapper.toAchievement(null));
    }

    @Test
    void toAchievement_allowsNullableOptionalFields() {
        AchievementDbEntity entity = new AchievementDbEntity();

        Achievement achievement = mapper.toAchievement(entity);

        assertNotNull(achievement);
        assertNull(achievement.getActivityType());
    }
}
