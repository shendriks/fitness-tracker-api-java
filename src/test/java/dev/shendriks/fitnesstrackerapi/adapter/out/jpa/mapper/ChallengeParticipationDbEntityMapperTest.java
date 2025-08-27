package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ChallengeDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ChallengeParticipationDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.ChallengeParticipation;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeParticipationDbEntityMapperTest {
    private final ChallengeParticipationDbEntityMapper mapper = Mappers.getMapper(ChallengeParticipationDbEntityMapper.class);

    private static ChallengeParticipationDbEntity buildParticipationEntity(
        String participationUlid,
        String challengeUlid,
        long userId,
        int percentage
    ) {
        return ChallengeParticipationDbEntity
            .builder()
            .id(1L)
            .ulid(participationUlid)
            .user(UserDbEntity
                .builder()
                .id(userId)
                .build()
            )
            .challenge(
                ChallengeDbEntity
                    .builder()
                    .id(100L)
                    .ulid(challengeUlid)
                    .name("Run 100km in 2025")
                    .description("Put on your running shoes and run 100km in 2025")
                    .imageFilePath("/images/challenges/run.png")
                    .activityType(ActivityType.RUNNING)
                    .activityMetric(ActivityMetric.TOTAL_DISTANCE)
                    .completionThreshold(100_000L)
                    .startDate(Instant.parse("2025-01-01T00:00:00Z"))
                    .endDate(Instant.parse("2026-01-01T00:00:00Z"))
                    .build()
            )
            .createdAt(Instant.parse("2025-02-01T12:34:56Z"))
            .updatedAt(Instant.parse("2025-02-02T12:34:56Z"))
            .percentageCompleted(percentage)
            .build();
    }

    @Test
    void toChallengeParticipation_mapsAllFieldsAndWrapIdsAndUserIdAndSetHasUserJoinedTrue() {
        ChallengeParticipationDbEntity entity = buildParticipationEntity(
            "TESTULID000000000000000001",
            "TESTULID000000000000000002",
            7L,
            42
        );

        ChallengeParticipation achtualChallengeParticipation = mapper.toChallengeParticipation(entity);

        assertInstanceOf(ChallengeParticipation.class, achtualChallengeParticipation);
        assertEquals(new ChallengeParticipationId(1L), achtualChallengeParticipation.id());
        assertEquals(new ChallengeParticipationUlid("TESTULID000000000000000001"), achtualChallengeParticipation.ulid());
        assertEquals(new UserId(7L), achtualChallengeParticipation.userId());
        assertEquals(Instant.parse("2025-02-01T12:34:56Z"), achtualChallengeParticipation.createdAt());
        assertEquals(Instant.parse("2025-02-02T12:34:56Z"), achtualChallengeParticipation.updatedAt());
        assertEquals(42, achtualChallengeParticipation.percentageCompleted());
        assertNotNull(achtualChallengeParticipation.challenge());
        assertEquals(new ChallengeId(100L), achtualChallengeParticipation.challenge().getId());
        assertEquals(new ChallengeUlid("TESTULID000000000000000002"), achtualChallengeParticipation.challenge().getUlid());
        assertEquals("Run 100km in 2025", achtualChallengeParticipation.challenge().getName());
        assertEquals("Put on your running shoes and run 100km in 2025", achtualChallengeParticipation.challenge().getDescription());
        assertEquals("/images/challenges/run.png", achtualChallengeParticipation.challenge().getImageFilePath());
        assertEquals(ActivityType.RUNNING, achtualChallengeParticipation.challenge().getActivityType());
        assertEquals(ActivityMetric.TOTAL_DISTANCE, achtualChallengeParticipation.challenge().getActivityMetric());
        assertEquals(100_000L, achtualChallengeParticipation.challenge().getCompletionThreshold());
        assertEquals(Instant.parse("2025-01-01T00:00:00Z"), achtualChallengeParticipation.challenge().getStartDate());
        assertEquals(Instant.parse("2026-01-01T00:00:00Z"), achtualChallengeParticipation.challenge().getEndDate());
        assertTrue(achtualChallengeParticipation.challenge().isHasUserJoined(), "Expected hasUserJoined to be true");
    }

    @Test
    void toChallengeParticipations_mapsList() {
        List<ChallengeParticipationDbEntity> entities = List.of(
            buildParticipationEntity("TESTULID000000000000000003", "TESTULID000000000000000004", 1L, 10),
            buildParticipationEntity("TESTULID000000000000000005", "TESTULID000000000000000006", 2L, 20)
        );

        List<ChallengeParticipation> actualChallengeParticipations = mapper.toChallengeParticipations(entities);

        assertEquals(2, actualChallengeParticipations.size());
        assertEquals(new ChallengeParticipationUlid("TESTULID000000000000000003"), actualChallengeParticipations.getFirst().ulid());
        assertEquals(10, actualChallengeParticipations.getFirst().percentageCompleted());
        assertEquals(new ChallengeUlid("TESTULID000000000000000004"), actualChallengeParticipations.getFirst().challenge().getUlid());
        assertTrue(actualChallengeParticipations.getFirst().challenge().isHasUserJoined());

        assertEquals(new ChallengeParticipationUlid("TESTULID000000000000000005"), actualChallengeParticipations.get(1).ulid());
        assertEquals(20, actualChallengeParticipations.get(1).percentageCompleted());
        assertEquals(new ChallengeUlid("TESTULID000000000000000006"), actualChallengeParticipations.get(1).challenge().getUlid());
        assertTrue(actualChallengeParticipations.get(1).challenge().isHasUserJoined());
    }

    @Test
    void toChallengeParticipation_withNullInput_returnsNull() {
        assertNull(mapper.toChallengeParticipation(null));
    }

    @Test
    void toChallengeParticipations_withNullInput_returnsNull() {
        assertNull(mapper.toChallengeParticipations(null));
    }

    @Test
    void toChallengeParticipations_withEmptyList_returnsEmptyList() {
        List<ChallengeParticipation> participations = mapper.toChallengeParticipations(List.of());
        assertEquals(List.of(), participations);
    }
}
