package dev.shendriks.fitnesstrackerapi.adapter.out.jpa;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.MilestoneDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.TrophyDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.Trophy;
import dev.shendriks.fitnesstrackerapi.domain.enums.AchievementType;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureTestEntityManager
class TrophyJpaRepositioryAdapterIntegrationTest extends JpaRepositioryAdapterIntegrationTest {
    private final TrophyJpaRepositioryAdapter adapter;

    public TrophyJpaRepositioryAdapterIntegrationTest(
        @Autowired TrophyJpaRepositioryAdapter adapter,
        @Autowired TestEntityManager entityManager
    ) {
        super(entityManager);
        this.adapter = adapter;
    }

    private MilestoneDbEntity createAndPersistMilestone() {
        MilestoneDbEntity milestone = MilestoneDbEntity
            .builder()
            .name("5k Distance")
            .description("Run total of 5 kilometers")
            .imageFilePath("milestone.png")
            .activityType(ActivityType.RUNNING)
            .activityMetric(ActivityMetric.TOTAL_DISTANCE)
            .completionThreshold(5_000L)
            .build();
        return entityManager.persist(milestone);
    }

    @Test
    void createThenFindThenExistsThenDeleteById_worksAsExpected() {
        UserId userId = createAndPersistUser();
        MilestoneDbEntity milestone = createAndPersistMilestone();
        MilestoneId milestoneId = new MilestoneId(milestone.getId());

        assertFalse(adapter.existsByUserAndAchievement(userId, milestoneId), "Expected no trophy before create");
        assertTrue(adapter.findAllByUser(userId).isEmpty(), "Expected no trophy before create");

        Trophy trophy = adapter.createTrophyForUserAndAchievement(userId, milestoneId);
        assertNotNull(trophy);
        assertInstanceOf(TrophyId.class, trophy.id());
        assertInstanceOf(TrophyUlid.class, trophy.ulid());
        assertEquals(userId, trophy.userId());
        assertEquals(AchievementType.MILESTONE, trophy.achievementType());
        assertNotNull(trophy.unlockedAt());
        assertNotNull(trophy.achievement());
        assertEquals(new AchievementId(milestone.getId()), trophy.achievement().getId());

        assertTrue(adapter.existsByUserAndAchievement(userId, milestoneId), "Expected trophy after create");
        List<Trophy> trophies = adapter.findAllByUser(userId);
        assertEquals(1, trophies.size(), "Expected one trophy");
        assertEquals(trophy.id(), trophies.getFirst().id());
        assertEquals(AchievementType.MILESTONE, trophies.getFirst().achievementType());

        adapter.deleteIfNotExistsByUserAndAchievement(userId, milestoneId);

        assertFalse(adapter.existsByUserAndAchievement(userId, milestoneId), "Expected no trophy after delete");
        assertTrue(adapter.findAllByUser(userId).isEmpty(), "Expected no trophy after delete");
        assertNull(entityManager.find(TrophyDbEntity.class, trophy.id().value()), "Expected no trophy after delete");
    }

    @Test
    void deleteByAchievementUlid_removesExistingTrophy() {
        UserId userId = createAndPersistUser();
        MilestoneDbEntity milestone = createAndPersistMilestone();
        MilestoneId achievementId = new MilestoneId(milestone.getId());
        MilestoneUlid achievementUlid = new MilestoneUlid(milestone.getUlid());

        Trophy trophy = adapter.createTrophyForUserAndAchievement(userId, achievementId);

        assertTrue(adapter.existsByUserAndAchievement(userId, achievementId), "Expected trophy after create");

        adapter.deleteIfNotExistsByUserAndAchievement(userId, achievementUlid);

        assertFalse(adapter.existsByUserAndAchievement(userId, achievementId), "Expected no trophy after delete");
        assertTrue(adapter.findAllByUser(userId).isEmpty(), "Expected no trophy after delete");
        assertNull(entityManager.find(TrophyDbEntity.class, trophy.id().value()), "Expected no trophy after delete");
    }
}
