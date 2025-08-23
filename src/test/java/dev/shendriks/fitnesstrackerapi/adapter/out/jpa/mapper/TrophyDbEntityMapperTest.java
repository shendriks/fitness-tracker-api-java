package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ChallengeDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.MilestoneDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.TrophyDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.Achievement;
import dev.shendriks.fitnesstrackerapi.domain.entity.Trophy;
import dev.shendriks.fitnesstrackerapi.domain.enums.AchievementType;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrophyDbEntityMapperTest {
    private final TrophyDbEntityMapper mapper;

    {
        TrophyDbEntityMapper mapper1 = Mappers.getMapper(TrophyDbEntityMapper.class);
        Field achievementDbEntityMapperField;
        try {
            achievementDbEntityMapperField = mapper1.getClass().getDeclaredField("achievementDbEntityMapper");
            achievementDbEntityMapperField.setAccessible(true);
            achievementDbEntityMapperField.set(mapper1, Mappers.getMapper(AchievementDbEntityMapper.class));
            mapper = mapper1;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static MilestoneDbEntity buildMilestoneAchievement(String ulid) {
        return MilestoneDbEntity
            .builder()
            .id(10L)
            .ulid(ulid)
            .name("10 Runs")
            .description("Complete 10 running activities")
            .imageFilePath("/images/milestones/10-runs.png")
            .activityType(ActivityType.RUNNING)
            .activityMetric(ActivityMetric.ACTIVITY_COUNT)
            .completionThreshold(10L)
            .build();
    }

    private static ChallengeDbEntity buildChallengeAchievement(String ulid) {
        return ChallengeDbEntity
            .builder()
            .id(20L)
            .ulid(ulid)
            .name("Run 100km in 2025")
            .description("Put on your running shoes and run 100km in 2025")
            .imageFilePath("/images/challenges/run.png")
            .activityType(ActivityType.RUNNING)
            .activityMetric(ActivityMetric.TOTAL_DISTANCE)
            .completionThreshold(100_000L)
            .startDate(Instant.parse("2025-01-01T00:00:00Z"))
            .endDate(Instant.parse("2026-01-01T00:00:00Z"))
            .build();
    }

    private static TrophyDbEntity buildTrophyEntity(
        long id,
        String ulid,
        long userId,
        Object achievement,
        Instant createdAt
    ) {
        TrophyDbEntity.TrophyDbEntityBuilder builder = TrophyDbEntity
            .builder()
            .id(id)
            .ulid(ulid)
            .user(UserDbEntity.builder().id(userId).build())
            .createdAt(createdAt)
            .updatedAt(createdAt);

        switch (achievement) {
            case MilestoneDbEntity milestoneDbEntity -> builder.achievement(milestoneDbEntity);
            case ChallengeDbEntity challengeDbEntity -> builder.achievement(challengeDbEntity);
            case null -> builder.achievement(null);
            default -> throw new IllegalArgumentException("Unsupported achievement type in test");
        }

        return builder.build();
    }

    @Test
    void toTrophy_shouldMapAllFields_forMilestoneAchievement() {
        Instant createdAt = Instant.parse("2025-08-22T12:00:00Z");
        TrophyDbEntity entity = buildTrophyEntity(
            1L,
            "TESTULID000000000000000001",
            7L,
            buildMilestoneAchievement("TESTULID000000000000000010"),
            createdAt
        );

        Trophy actualTrophy = mapper.toTrophy(entity);

        assertNotNull(actualTrophy);
        assertEquals(new TrophyId(1L), actualTrophy.id());
        assertEquals(new TrophyUlid("TESTULID000000000000000001"), actualTrophy.ulid());
        assertEquals(new UserId(7L), actualTrophy.userId());
        assertEquals(AchievementType.MILESTONE, actualTrophy.achievementType());
        assertEquals(createdAt, actualTrophy.unlockedAt());
        Achievement achievement = actualTrophy.achievement();
        assertNotNull(achievement);
        assertEquals(new AchievementId(10L), achievement.getId());
        assertEquals(new AchievementUlid("TESTULID000000000000000010"), achievement.getUlid());
        assertEquals("10 Runs", achievement.getName());
        assertEquals("Complete 10 running activities", achievement.getDescription());
        assertEquals("/images/milestones/10-runs.png", achievement.getImageFilePath());
        assertEquals(ActivityType.RUNNING, achievement.getActivityType());
        assertEquals(ActivityMetric.ACTIVITY_COUNT, achievement.getActivityMetric());
        assertEquals(10L, achievement.getCompletionThreshold());
    }

    @Test
    void toTrophy_shouldMapAllFields_forChallengeAchievement() {
        Instant createdAt = Instant.parse("2025-08-21T09:00:00Z");
        TrophyDbEntity entity = buildTrophyEntity(
            2L,
            "TESTULID000000000000000002",
            8L,
            buildChallengeAchievement("TESTULID000000000000000020"),
            createdAt
        );

        Trophy actualTrophy = mapper.toTrophy(entity);

        assertNotNull(actualTrophy);
        assertEquals(new TrophyId(2L), actualTrophy.id());
        assertEquals(new TrophyUlid("TESTULID000000000000000002"), actualTrophy.ulid());
        assertEquals(new UserId(8L), actualTrophy.userId());
        assertEquals(AchievementType.CHALLENGE, actualTrophy.achievementType());
        assertEquals(createdAt, actualTrophy.unlockedAt());
        Achievement achievement = actualTrophy.achievement();
        assertNotNull(achievement);
        assertEquals(new AchievementId(20L), achievement.getId());
        assertEquals(new AchievementUlid("TESTULID000000000000000020"), achievement.getUlid());
        assertEquals("Run 100km in 2025", achievement.getName());
        assertEquals("Put on your running shoes and run 100km in 2025", achievement.getDescription());
        assertEquals("/images/challenges/run.png", achievement.getImageFilePath());
        assertEquals(ActivityType.RUNNING, achievement.getActivityType());
        assertEquals(ActivityMetric.TOTAL_DISTANCE, achievement.getActivityMetric());
        assertEquals(100_000L, achievement.getCompletionThreshold());
    }

    @Test
    void toTrophies_shouldMapList_mixedAchievements() {
        List<TrophyDbEntity> entities = List.of(
            buildTrophyEntity(
                3L,
                "TESTULID000000000000000003",
                1L,
                buildMilestoneAchievement("TESTULID000000000000000011"),
                Instant.parse("2025-08-20T00:00:00Z")
            ),
            buildTrophyEntity(
                4L,
                "TESTULID000000000000000004",
                2L,
                buildChallengeAchievement("TESTULID000000000000000021"),
                Instant.parse("2025-08-19T00:00:00Z")
            )
        );

        List<Trophy> actualTrophies = mapper.toTrophies(entities);

        assertEquals(2, actualTrophies.size());
        assertEquals(new TrophyUlid("TESTULID000000000000000003"), actualTrophies.getFirst().ulid());
        assertEquals(AchievementType.MILESTONE, actualTrophies.getFirst().achievementType());
        assertEquals(new TrophyUlid("TESTULID000000000000000004"), actualTrophies.get(1).ulid());
        assertEquals(AchievementType.CHALLENGE, actualTrophies.get(1).achievementType());
    }

    @Test
    void toTrophy_withNullInput_returnsNull() {
        assertNull(mapper.toTrophy(null));
    }

    @Test
    void toTrophies_withNullInput_returnsNull() {
        assertNull(mapper.toTrophies(null));
    }

    @Test
    void toTrophy_withNullAchievement_throwsException() {
        TrophyDbEntity entity = buildTrophyEntity(
            5L,
            "TESTULID000000000000000005",
            9L,
            null,
            Instant.parse("2025-08-18T00:00:00Z")
        );
        assertThrows(RuntimeException.class, () -> mapper.toTrophy(entity));
    }
}
