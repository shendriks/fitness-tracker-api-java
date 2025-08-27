package dev.shendriks.fitnesstrackerapi.adapter.out.jpa;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.MilestoneDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.TrophyDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.Milestone;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.MilestoneId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureTestEntityManager
class MilestoneJpaRepositioryAdapterIntegrationTest extends JpaRepositioryAdapterIntegrationTest {
    private final MilestoneJpaRepositioryAdapter adapter;

    public MilestoneJpaRepositioryAdapterIntegrationTest(
        @Autowired MilestoneJpaRepositioryAdapter adapter,
        @Autowired TestEntityManager entityManager
    ) {
        super(entityManager);
        this.adapter = adapter;
    }

    private MilestoneDbEntity createAndPersistMilestone(String name, long threshold) {
        MilestoneDbEntity milestone = MilestoneDbEntity
            .builder()
            .name(name)
            .description(name + " desc")
            .imageFilePath("milestone.png")
            .activityType(ActivityType.RUNNING)
            .activityMetric(ActivityMetric.TOTAL_DISTANCE)
            .completionThreshold(threshold)
            .build();
        return entityManager.persist(milestone);
    }

    private void createTrophyFor(UserId userId, MilestoneDbEntity milestone) {
        UserDbEntity user = entityManager.find(UserDbEntity.class, userId.value());
        TrophyDbEntity trophy = TrophyDbEntity
            .builder()
            .user(user)
            .achievement(milestone)
            .build();
        entityManager.persist(trophy);
    }

    @Test
    void findAllWithCompletedByUser_returnsAllWithCorrectCompletionFlagsAndOrder() {
        UserId userId = createAndPersistUser();
        MilestoneDbEntity milestone1 = createAndPersistMilestone("5k Distance", 5_000L);
        MilestoneDbEntity milestone2 = createAndPersistMilestone("10k Distance", 10_000L);

        List<Milestone> actualMilestonesBefore = adapter.findAllWithCompletedByUser(userId);

        assertEquals(2, actualMilestonesBefore.size(), "Expected all milestones to be returned");
        assertEquals(new MilestoneId(milestone1.getId()), actualMilestonesBefore.get(0).getId());
        assertFalse(actualMilestonesBefore.get(0).isCompleted(), "Expected isCompleted=false initially for first");
        assertEquals(new MilestoneId(milestone2.getId()), actualMilestonesBefore.get(1).getId());
        assertFalse(actualMilestonesBefore.get(1).isCompleted(), "Expected isCompleted=false initially for second");

        createTrophyFor(userId, milestone2);

        List<Milestone> actualMilestonesAfter = adapter.findAllWithCompletedByUser(userId);

        assertEquals(2, actualMilestonesAfter.size());
        Milestone first = actualMilestonesAfter.get(0);
        Milestone second = actualMilestonesAfter.get(1);
        assertEquals(new MilestoneId(milestone1.getId()), first.getId());
        assertFalse(first.isCompleted(), "Expected milestone1 to remain not completed");
        assertEquals(new MilestoneId(milestone2.getId()), second.getId());
        assertTrue(second.isCompleted(), "Expected milestone2 to be marked completed due to trophy");

        assertEquals("5k Distance", first.getName());
        assertEquals("10k Distance", second.getName());
        assertEquals(ActivityType.RUNNING, second.getActivityType());
        assertEquals(ActivityMetric.TOTAL_DISTANCE, second.getActivityMetric());
        assertEquals(10_000L, second.getCompletionThreshold());
    }

    @Test
    void findNameById_returnsExpected() {
        MilestoneDbEntity milestone = createAndPersistMilestone("Half Marathon", 21_097L);

        Optional<String> actualName = adapter.findNameById(new MilestoneId(milestone.getId()));

        assertTrue(actualName.isPresent());
        assertEquals("Half Marathon", actualName.get());
    }
}
