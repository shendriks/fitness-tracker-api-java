package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection.ChallengeProjection;
import dev.shendriks.fitnesstrackerapi.domain.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeId;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeUlid;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeDbEntityMapperTest {
    private final ChallengeDbEntityMapper mapper = Mappers.getMapper(ChallengeDbEntityMapper.class);

    @Test
    void toChallenge_shouldMapAllFields_andWrapIdTypes() {
        ChallengeProjection projection = ChallengeProjection
            .builder()
            .id(42L)
            .ulid("TESTULID000000000000000001")
            .name("Run 100km")
            .description("Complete 100km in 2025")
            .imageFilePath("/images/challenges/run-100.png")
            .activityType(ActivityType.RUNNING)
            .activityMetric(ActivityMetric.TOTAL_DISTANCE)
            .completionThreshold(100_000L)
            .startDate(Instant.parse("2025-01-01T00:00:00Z"))
            .endDate(Instant.parse("2026-01-01T00:00:00Z"))
            .hasUserJoined(true)
            .build();

        Challenge challenge = mapper.toChallenge(projection);

        assertInstanceOf(Challenge.class, challenge);
        assertEquals(new ChallengeId(42L), challenge.getId());
        assertEquals(new ChallengeUlid("TESTULID000000000000000001"), challenge.getUlid());
        assertEquals("Run 100km", challenge.getName());
        assertEquals("Complete 100km in 2025", challenge.getDescription());
        assertEquals("/images/challenges/run-100.png", challenge.getImageFilePath());
        assertEquals(ActivityType.RUNNING, challenge.getActivityType());
        assertEquals(ActivityMetric.TOTAL_DISTANCE, challenge.getActivityMetric());
        assertEquals(100_000L, challenge.getCompletionThreshold());
        assertEquals(Instant.parse("2025-01-01T00:00:00Z"), challenge.getStartDate());
        assertEquals(Instant.parse("2026-01-01T00:00:00Z"), challenge.getEndDate());
        assertTrue(challenge.isHasUserJoined());
    }

    @Test
    void toChallenges_shouldMapList() {
        List<ChallengeProjection> projections = List.of(
            ChallengeProjection
                .builder()
                .id(1L)
                .ulid("TESTULID000000000000000002")
                .name("Challenge A")
                .description("Desc A")
                .imageFilePath("/images/a.png")
                .activityType(ActivityType.CYCLING)
                .activityMetric(ActivityMetric.TOTAL_DURATION)
                .completionThreshold(10_000L)
                .startDate(Instant.parse("2025-01-10T00:00:00Z"))
                .endDate(Instant.parse("2025-02-10T00:00:00Z"))
                .hasUserJoined(true)
                .build(),
            ChallengeProjection
                .builder()
                .id(2L)
                .ulid("TESTULID000000000000000003")
                .name("Challenge B")
                .description("Desc B")
                .imageFilePath("/images/b.png")
                .activityType(ActivityType.WALKING)
                .activityMetric(ActivityMetric.ACTIVITY_COUNT)
                .completionThreshold(20_000L)
                .startDate(Instant.parse("2025-03-01T00:00:00Z"))
                .endDate(Instant.parse("2025-04-01T00:00:00Z"))
                .hasUserJoined(false)
                .build()
        );

        List<Challenge> actualChallenges = mapper.toChallenges(projections);

        assertEquals(2, actualChallenges.size());
        assertEquals(new ChallengeId(1L), actualChallenges.getFirst().getId());
        assertEquals(new ChallengeUlid("TESTULID000000000000000002"), actualChallenges.getFirst().getUlid());
        assertEquals("Challenge A", actualChallenges.getFirst().getName());
        assertEquals("Desc A", actualChallenges.getFirst().getDescription());
        assertEquals("/images/a.png", actualChallenges.getFirst().getImageFilePath());
        assertEquals(ActivityType.CYCLING, actualChallenges.getFirst().getActivityType());
        assertEquals(ActivityMetric.TOTAL_DURATION, actualChallenges.getFirst().getActivityMetric());
        assertEquals(10_000L, actualChallenges.getFirst().getCompletionThreshold());
        assertEquals(Instant.parse("2025-01-10T00:00:00Z"), actualChallenges.getFirst().getStartDate());
        assertEquals(Instant.parse("2025-02-10T00:00:00Z"), actualChallenges.getFirst().getEndDate());
        assertTrue(actualChallenges.getFirst().isHasUserJoined());
        assertEquals(new ChallengeId(2L), actualChallenges.get(1).getId());
        assertEquals(new ChallengeUlid("TESTULID000000000000000003"), actualChallenges.get(1).getUlid());
        assertEquals("Challenge B", actualChallenges.get(1).getName());
        assertEquals("Desc B", actualChallenges.get(1).getDescription());
        assertEquals("/images/b.png", actualChallenges.get(1).getImageFilePath());
        assertEquals(ActivityType.WALKING, actualChallenges.get(1).getActivityType());
        assertEquals(ActivityMetric.ACTIVITY_COUNT, actualChallenges.get(1).getActivityMetric());
        assertEquals(20_000L, actualChallenges.get(1).getCompletionThreshold());
        assertEquals(Instant.parse("2025-03-01T00:00:00Z"), actualChallenges.get(1).getStartDate());
        assertEquals(Instant.parse("2025-04-01T00:00:00Z"), actualChallenges.get(1).getEndDate());
        assertFalse(actualChallenges.get(1).isHasUserJoined());
    }

    @Test
    void toChallenge_withNullInput_returnsNull() {
        assertNull(mapper.toChallenge(null));
    }

    @Test
    void toChallenges_withNullInput_returnsNull() {
        assertNull(mapper.toChallenges(null));
    }
}
