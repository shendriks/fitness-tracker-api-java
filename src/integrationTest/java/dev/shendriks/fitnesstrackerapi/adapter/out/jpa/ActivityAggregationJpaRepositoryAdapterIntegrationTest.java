package dev.shendriks.fitnesstrackerapi.adapter.out.jpa;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ActivityDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityAggregation;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityAggregationMap;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureTestEntityManager
class ActivityAggregationJpaRepositoryAdapterIntegrationTest extends JpaRepositioryAdapterIntegrationTest {
    private final ActivityAggregationJpaRepositoryAdapter adapter;

    public ActivityAggregationJpaRepositoryAdapterIntegrationTest(
        @Autowired ActivityAggregationJpaRepositoryAdapter adapter,
        @Autowired TestEntityManager entityManager
    ) {
        super(entityManager);
        this.adapter = adapter;
    }

    private void persistActivity(UserId userId, ActivityType type, int duration, int distance, Instant startDate, String title) {
        UserDbEntity user = entityManager.find(UserDbEntity.class, userId.value());
        ActivityDbEntity entity = ActivityDbEntity
            .builder()
            .user(user)
            .activityType(type)
            .duration(duration)
            .calories(0)
            .title(title)
            .description(title + " desc")
            .distance(distance)
            .startDate(startDate)
            .build();
        entityManager.persist(entity);
        entityManager.flush();
    }

    @Test
    void aggregateForUserInTimeRange_and_allTime_endToEnd() {
        UserId userId = createAndPersistUser();

        Instant from = Instant.parse("2025-08-20T00:00:00Z");
        Instant to = Instant.parse("2025-08-21T00:00:00Z");

        Instant outside = Instant.parse("2025-08-19T10:00:00Z"); // outside time range
        Instant inside1 = Instant.parse("2025-08-20T10:00:00Z"); // inside time range
        Instant inside2 = Instant.parse("2025-08-20T12:00:00Z"); // inside time range

        persistActivity(userId, ActivityType.RUNNING, 100, 500, outside, "old run");
        persistActivity(userId, ActivityType.RUNNING, 300, 1000, inside1, "run");
        persistActivity(userId, ActivityType.CYCLING, 600, 2000, inside2, "ride");

        // Aggregate in time range
        ActivityAggregationMap actualActivityAggregationMap = adapter.aggregateForUserInTimeRange(userId, from, to);
        ActivityAggregation totalInRange = actualActivityAggregationMap.getTotal();

        // Totals should reflect only in-range activities
        assertEquals(2L, totalInRange.count());
        assertEquals(3000L, totalInRange.totalDistance());
        assertEquals(900L, totalInRange.totalDuration());
        assertEquals(2000L, totalInRange.maxDistance());
        assertEquals(600L, totalInRange.maxDuration());

        // Per-type in-range
        ActivityAggregation runningInRange = actualActivityAggregationMap.getByType(ActivityType.RUNNING);
        assertEquals(1L, runningInRange.count());
        assertEquals(1000L, runningInRange.totalDistance());
        assertEquals(300L, runningInRange.totalDuration());
        assertEquals(1000L, runningInRange.maxDistance());
        assertEquals(300L, runningInRange.maxDuration());

        ActivityAggregation cyclingInRange = actualActivityAggregationMap.getByType(ActivityType.CYCLING);
        assertEquals(1L, cyclingInRange.count());
        assertEquals(2000L, cyclingInRange.totalDistance());
        assertEquals(600L, cyclingInRange.totalDuration());
        assertEquals(2000L, cyclingInRange.maxDistance());
        assertEquals(600L, cyclingInRange.maxDuration());

        // A type with no activities should be zeroed
        ActivityAggregation swimmingInRange = actualActivityAggregationMap.getByType(ActivityType.SWIMMING);
        assertEquals(0L, swimmingInRange.count());
        assertEquals(0L, swimmingInRange.totalDistance());
        assertEquals(0L, swimmingInRange.totalDuration());
        assertEquals(0L, swimmingInRange.maxDistance());
        assertEquals(0L, swimmingInRange.maxDuration());

        // Aggregate without time range
        ActivityAggregationMap actualAllTimeAggregationMap = adapter.aggregateForUser(userId);
        ActivityAggregation totalAll = actualAllTimeAggregationMap.getTotal();

        // Now all 3 activities are included
        assertEquals(3L, totalAll.count());
        assertEquals(3500L, totalAll.totalDistance());
        assertEquals(1000L, totalAll.totalDuration());
        assertEquals(2000L, totalAll.maxDistance());
        assertEquals(600L, totalAll.maxDuration());

        // Per-type for all time
        ActivityAggregation runningAll = actualAllTimeAggregationMap.getByType(ActivityType.RUNNING);
        assertEquals(2L, runningAll.count());
        assertEquals(1500L, runningAll.totalDistance());
        assertEquals(400L, runningAll.totalDuration());
        assertEquals(1000L, runningAll.maxDistance());
        assertEquals(300L, runningAll.maxDuration());

        ActivityAggregation cyclingAll = actualAllTimeAggregationMap.getByType(ActivityType.CYCLING);
        assertEquals(1L, cyclingAll.count());
        assertEquals(2000L, cyclingAll.totalDistance());
        assertEquals(600L, cyclingAll.totalDuration());
        assertEquals(2000L, cyclingAll.maxDistance());
        assertEquals(600L, cyclingAll.maxDuration());

        // A type with no activities should be zeroed
        ActivityAggregation swimmingAll = actualAllTimeAggregationMap.getByType(ActivityType.SWIMMING);
        assertEquals(0L, swimmingAll.count());
        assertEquals(0L, swimmingAll.totalDistance());
        assertEquals(0L, swimmingAll.totalDuration());
        assertEquals(0L, swimmingAll.maxDistance());
        assertEquals(0L, swimmingAll.maxDuration());
    }
}
