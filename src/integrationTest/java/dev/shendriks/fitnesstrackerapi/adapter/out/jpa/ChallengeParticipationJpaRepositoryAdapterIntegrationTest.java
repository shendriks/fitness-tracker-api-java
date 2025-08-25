package dev.shendriks.fitnesstrackerapi.adapter.out.jpa;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ChallengeDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ChallengeParticipationDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.application.exception.ChallengeNotFoundException;
import dev.shendriks.fitnesstrackerapi.domain.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.entity.ChallengeParticipation;
import dev.shendriks.fitnesstrackerapi.domain.enums.AccountType;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureTestEntityManager
class ChallengeParticipationJpaRepositoryAdapterIntegrationTest {
    private final ChallengeParticipationJpaRepositoryAdapter adapter;
    private final TestEntityManager entityManager;

    public ChallengeParticipationJpaRepositoryAdapterIntegrationTest(
        @Autowired ChallengeParticipationJpaRepositoryAdapter adapter,
        @Autowired TestEntityManager entityManager
    ) {
        this.adapter = adapter;
        this.entityManager = entityManager;
    }

    private UserId createAndPersistUser() {
        UserDbEntity user = UserDbEntity
            .builder()
            .name("Alice")
            .email("alice@example.com")
            .password("password")
            .authority("ROLE_USER")
            .accountType(AccountType.BASIC)
            .build();
        Long userId = entityManager.persistAndGetId(user, Long.class);
        return new UserId(userId);
    }

    private ChallengeDbEntity createAndPersistChallenge(String ulid, Instant start, Instant end) {
        ChallengeDbEntity challenge = ChallengeDbEntity
            .builder()
            .name("10k Run")
            .description("Run 10 kilometers")
            .imageFilePath("challenge.png")
            .activityType(ActivityType.RUNNING)
            .activityMetric(ActivityMetric.TOTAL_DISTANCE)
            .completionThreshold(10_000L)
            .startDate(start)
            .endDate(end)
            .ulid(ulid)
            .build();
        return entityManager.persist(challenge);
    }

    @Test
    void joinChallenge_update_findCurrent_leave_endToEnd() {
        UserId userId = createAndPersistUser();
        String challengeUlid = "TESTULID000000000000000001";
        ChallengeDbEntity challengeDbEntity = createAndPersistChallenge(
            challengeUlid,
            Instant.now().minusSeconds(3600),
            Instant.now().plusSeconds(3600)
        );

        boolean existsBefore = adapter.existsByChallengeAndUser(userId, new ChallengeUlid(challengeUlid));
        assertFalse(existsBefore, "Expected no participation before create");

        Challenge challenge = Challenge
            .builder()
            .id(new ChallengeId(challengeDbEntity.getId()))
            .ulid(new ChallengeUlid(challengeDbEntity.getUlid()))
            .name(challengeDbEntity.getName())
            .description(challengeDbEntity.getDescription())
            .imageFilePath(challengeDbEntity.getImageFilePath())
            .activityType(challengeDbEntity.getActivityType())
            .activityMetric(challengeDbEntity.getActivityMetric())
            .completionThreshold(challengeDbEntity.getCompletionThreshold())
            .startDate(challengeDbEntity.getStartDate())
            .endDate(challengeDbEntity.getEndDate())
            .hasUserJoined(false)
            .build();

        ChallengeParticipation actualChallengeParticipation = adapter.joinChallenge(userId, challenge.getUlid());

        assertInstanceOf(ChallengeParticipation.class, actualChallengeParticipation);
        assertInstanceOf(ChallengeParticipationId.class, actualChallengeParticipation.id());
        assertInstanceOf(ChallengeParticipationUlid.class, actualChallengeParticipation.ulid());
        assertEquals(userId, actualChallengeParticipation.userId());
        assertEquals(challenge.getId(), actualChallengeParticipation.challenge().getId());

        assertTrue(adapter.existsByChallengeAndUser(userId, challenge.getUlid()), "Expected participation after join");
        adapter.updatePercentageCompleted(actualChallengeParticipation.id(), 42);

        List<ChallengeParticipation> challengeParticipations = adapter.findCurrentByUser(userId);
        assertEquals(1, challengeParticipations.size());
        assertEquals(42, challengeParticipations.getFirst().percentageCompleted());
        assertTrue(challengeParticipations.getFirst().challenge().isHasUserJoined(), "Expected hasUserJoined=true for mapped challenge");
        assertEquals(challenge.getId(), challengeParticipations.getFirst().challenge().getId());

        adapter.leaveChallenge(userId, challenge.getUlid());
        assertFalse(adapter.existsByChallengeAndUser(userId, challenge.getUlid()));
        assertTrue(adapter.findCurrentByUser(userId).isEmpty());

        assertNull(entityManager.find(ChallengeParticipationDbEntity.class, challenge.getId().getValue()));
    }

    @Test
    void joinChallenge_whenChallengeNotExists_throws() {
        UserId userId = createAndPersistUser();
        assertThrows(ChallengeNotFoundException.class, () -> adapter.joinChallenge(
            userId,
            new ChallengeUlid("SOMENONEXISTINGULID0000000")
        ));
    }

    @Test
    void leaveChallenge_whenChallengeNotExists_throws() {
        UserId userId = createAndPersistUser();
        assertThrows(ChallengeNotFoundException.class, () -> adapter.leaveChallenge(
            userId,
            new ChallengeUlid("SOMENONEXISTINGULID0000000")
        ));
    }
}
