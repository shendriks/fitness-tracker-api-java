package dev.shendriks.fitnesstrackerapi.adapter.out.jpa;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ChallengeDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ChallengeParticipationDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeId;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureTestEntityManager
class ChallengeJpaRepositioryAdapterIntegrationTest extends JpaRepositioryAdapterIntegrationTest {
    private final ChallengeJpaRepositioryAdapter adapter;
    @MockitoBean
    private Clock clock;
    private Instant now;

    public ChallengeJpaRepositioryAdapterIntegrationTest(
        @Autowired ChallengeJpaRepositioryAdapter adapter,
        @Autowired TestEntityManager entityManager
    ) {
        super(entityManager);
        this.adapter = adapter;
    }

    private ChallengeDbEntity createAndPersistChallenge(String ulid, String name, Instant start, Instant end) {
        ChallengeDbEntity challenge = ChallengeDbEntity
            .builder()
            .name(name)
            .description(name + " desc")
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

    private void createParticipationForUser(UserId userId, ChallengeDbEntity challenge) {
        UserDbEntity user = entityManager.find(UserDbEntity.class, userId.value());
        ChallengeParticipationDbEntity challengeParticipation = ChallengeParticipationDbEntity
            .builder()
            .user(user)
            .challenge(challenge)
            .percentageCompleted(0)
            .build();
        entityManager.persist(challengeParticipation);
    }

    @BeforeEach
    void setUp() {
        when(clock.instant()).thenReturn(Instant.parse("2025-08-01T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
        now = Instant.now(clock);
    }

    @Test
    void findAllByUser_returnsOnlyCurrentWithHasUserJoinedFlag() {
        UserId userId = createAndPersistUser();

        ChallengeDbEntity current = createAndPersistChallenge(
            "TESTULID000000000000000001",
            "Current Challenge",
            now.minus(1, ChronoUnit.DAYS),
            now.plus(1, ChronoUnit.DAYS)
        );
        createAndPersistChallenge(
            "TESTULID000000000000000002",
            "Past Challenge",
            now.minus(2, ChronoUnit.DAYS),
            now.minus(1, ChronoUnit.DAYS)
        );
        createAndPersistChallenge(
            "TESTULID000000000000000003",
            "Future Challenge",
            now.plus(1, ChronoUnit.DAYS),
            now.plus(2, ChronoUnit.DAYS)
        );

        List<Challenge> actualChallengesBeforeJoin = adapter.findAllByUser(userId);
        assertEquals(1, actualChallengesBeforeJoin.size(), "Expected only current challenges");
        Challenge challengeBefore = actualChallengesBeforeJoin.getFirst();
        assertEquals(new ChallengeId(current.getId()), challengeBefore.getId());
        assertFalse(challengeBefore.isHasUserJoined(), "Expected hasUserJoined=false initially");

        createParticipationForUser(userId, current);

        List<Challenge> actualChallengesAfterJoin = adapter.findAllByUser(userId);
        assertEquals(1, actualChallengesAfterJoin.size());
        Challenge challengeAfter = actualChallengesAfterJoin.getFirst();
        assertEquals(new ChallengeId(current.getId()), challengeAfter.getId());
        assertTrue(challengeAfter.isHasUserJoined(), "Expected hasUserJoined=true after participation");
    }

    @Test
    void findByUserIdAndUlid_returnsCorrectChallengeAndHasUserJoinedReflectingParticipation() {
        UserId userId = createAndPersistUser();

        ChallengeDbEntity challengeDbEntity = createAndPersistChallenge(
            "TESTULID000000000000000004",
            "Lookup Challenge",
            now.minus(1, ChronoUnit.DAYS),
            now.plus(1, ChronoUnit.DAYS)
        );

        Optional<Challenge> before = adapter.findByUserIdAndUlid(userId, new ChallengeUlid(challengeDbEntity.getUlid()));
        assertTrue(before.isPresent());
        assertEquals(new ChallengeId(challengeDbEntity.getId()), before.get().getId());
        assertFalse(before.get().isHasUserJoined(), "Expected hasUserJoined=false initially");

        createParticipationForUser(userId, challengeDbEntity);

        Optional<Challenge> after = adapter.findByUserIdAndUlid(userId, new ChallengeUlid(challengeDbEntity.getUlid()));
        assertTrue(after.isPresent());
        assertEquals(new ChallengeId(challengeDbEntity.getId()), after.get().getId());
        assertTrue(after.get().isHasUserJoined(), "Expected hasUserJoined=true after participation");
    }

    @Test
    void findNameById_returnExpected() {
        ChallengeDbEntity challengeDbEntity = createAndPersistChallenge(
            "TESTULID000000000000000005",
            "Name Challenge",
            now.minus(1, ChronoUnit.DAYS),
            now.plus(1, ChronoUnit.DAYS)
        );

        Optional<String> byId = adapter.findNameById(new ChallengeId(challengeDbEntity.getId()));

        assertTrue(byId.isPresent());
        assertEquals("Name Challenge", byId.get());
    }    
    
    @Test
    void findNameByUlid_returnExpected() {
        ChallengeDbEntity challengeDbEntity = createAndPersistChallenge(
            "TESTULID000000000000000006",
            "Name Challenge",
            now.minus(1, ChronoUnit.DAYS),
            now.plus(1, ChronoUnit.DAYS)
        );

        Optional<String> byUlid = adapter.findNameByUlid(new ChallengeUlid(challengeDbEntity.getUlid()));

        assertTrue(byUlid.isPresent());
        assertEquals("Name Challenge", byUlid.get());
    }
}
