package dev.shendriks.fitnesstrackerapi.adapter.out.jpa;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ActivityDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import dev.shendriks.fitnesstrackerapi.infrastructure.Constant;
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

    private void persistActivity(UserId userId, ActivityType type, Long duration, Double distance, Instant startDate, String title) {
        UserDbEntity user = entityManager.find(UserDbEntity.class, userId.value());
        ActivityDbEntity entity = ActivityDbEntity
            .builder()
            .user(user)
            .activityType(type)
            .duration(Duration.ofSeconds(duration))
            .distance(Distance.ofMeters(distance))
            .averageSpeed(Speed.ofMetersPerSecond(distance / duration))
            .title(title)
            .description(title + " desc")
            .startDate(startDate)
            .build();
        entityManager.persist(entity);
        entityManager.flush();
    }

    @Test
    void aggregateForUserInTimeRange_aggregatesCorrectly() {
        UserId userId = createAndPersistUser();

        Instant from = Instant.parse("2025-08-20T00:00:00Z");
        Instant to = Instant.parse("2025-08-21T00:00:00Z");

        Instant outside = Instant.parse("2025-08-19T10:00:00Z"); // outside time range
        Instant inside1 = Instant.parse("2025-08-20T10:00:00Z"); // inside time range
        Instant inside2 = Instant.parse("2025-08-20T12:00:00Z"); // inside time range

        persistActivity(userId, ActivityType.RUNNING, 100L, 500.0, outside, "old run");
        persistActivity(userId, ActivityType.RUNNING, 300L, 1000.0, inside1, "run");
        persistActivity(userId, ActivityType.CYCLING, 600L, 2000.0, inside2, "ride");

        // Aggregate in time range
        ActivityAggregationMap actualActivityAggregationMap = adapter.aggregateForUserInTimeRange(userId, from, to);
        ActivityAggregation totalInRange = actualActivityAggregationMap.getTotal();

        // Totals should reflect only in-range activities
        assertEquals(2L, totalInRange.count());
        assertEquals(3000.0, totalInRange.totalDistance().toMeters(), Constant.EPSILON);
        assertEquals(900L, totalInRange.totalDuration().toSeconds());
        assertEquals(2000.0, totalInRange.maxDistance().toMeters(), Constant.EPSILON);
        assertEquals(600L, totalInRange.maxDuration().toSeconds());

        // Per-type in-range
        ActivityAggregation runningInRange = actualActivityAggregationMap.getByType(ActivityType.RUNNING);
        assertEquals(1L, runningInRange.count());
        assertEquals(1000.0, runningInRange.totalDistance().toMeters(), Constant.EPSILON);
        assertEquals(300L, runningInRange.totalDuration().toSeconds());
        assertEquals(1000.0, runningInRange.maxDistance().toMeters(), Constant.EPSILON);
        assertEquals(300L, runningInRange.maxDuration().toSeconds());

        ActivityAggregation cyclingInRange = actualActivityAggregationMap.getByType(ActivityType.CYCLING);
        assertEquals(1L, cyclingInRange.count());
        assertEquals(2000.0, cyclingInRange.totalDistance().toMeters(), Constant.EPSILON);
        assertEquals(600L, cyclingInRange.totalDuration().toSeconds());
        assertEquals(2000.0, cyclingInRange.maxDistance().toMeters(), Constant.EPSILON);
        assertEquals(600L, cyclingInRange.maxDuration().toSeconds());

        // A type with no activities should be zeroed
        ActivityAggregation swimmingInRange = actualActivityAggregationMap.getByType(ActivityType.SWIMMING);
        assertEquals(0L, swimmingInRange.count());
        assertEquals(0.0, swimmingInRange.totalDistance().toMeters(), Constant.EPSILON);
        assertEquals(0L, swimmingInRange.totalDuration().toSeconds());
        assertEquals(0.0, swimmingInRange.maxDistance().toMeters(), Constant.EPSILON);
        assertEquals(0L, swimmingInRange.maxDuration().toSeconds());
    }

    @Test
    void aggregateForUserAllTime_aggregatesCorrectly() {
        UserId userId = createAndPersistUser();

        Instant outside = Instant.parse("2025-08-19T10:00:00Z"); // outside time range
        Instant inside1 = Instant.parse("2025-08-20T10:00:00Z"); // inside time range
        Instant inside2 = Instant.parse("2025-08-20T12:00:00Z"); // inside time range

        persistActivity(userId, ActivityType.RUNNING, 100L, 500.0, outside, "old run");
        persistActivity(userId, ActivityType.RUNNING, 300L, 1000.0, inside1, "run");
        persistActivity(userId, ActivityType.CYCLING, 600L, 2000.0, inside2, "ride");

        // Aggregate without time range
        ActivityAggregationMap actualAllTimeAggregationMap = adapter.aggregateForUser(userId);
        ActivityAggregation totalAll = actualAllTimeAggregationMap.getTotal();

        // Now all 3 activities are included
        assertEquals(3L, totalAll.count());
        assertEquals(3500.0, totalAll.totalDistance().toMeters(), Constant.EPSILON);
        assertEquals(1000L, totalAll.totalDuration().toSeconds());
        assertEquals(2000.0, totalAll.maxDistance().toMeters(), Constant.EPSILON);
        assertEquals(600L, totalAll.maxDuration().toSeconds());

        // Per-type for all time
        ActivityAggregation runningAll = actualAllTimeAggregationMap.getByType(ActivityType.RUNNING);
        assertEquals(2L, runningAll.count());
        assertEquals(1500.0, runningAll.totalDistance().toMeters(), Constant.EPSILON);
        assertEquals(400L, runningAll.totalDuration().toSeconds());
        assertEquals(1000.0, runningAll.maxDistance().toMeters(), Constant.EPSILON);
        assertEquals(300L, runningAll.maxDuration().toSeconds());

        ActivityAggregation cyclingAll = actualAllTimeAggregationMap.getByType(ActivityType.CYCLING);
        assertEquals(1L, cyclingAll.count());
        assertEquals(2000.0, cyclingAll.totalDistance().toMeters(), Constant.EPSILON);
        assertEquals(600L, cyclingAll.totalDuration().toSeconds());
        assertEquals(2000.0, cyclingAll.maxDistance().toMeters(), Constant.EPSILON);
        assertEquals(600L, cyclingAll.maxDuration().toSeconds());

        // A type with no activities should be zeroed
        ActivityAggregation swimmingAll = actualAllTimeAggregationMap.getByType(ActivityType.SWIMMING);
        assertEquals(0L, swimmingAll.count());
        assertEquals(0.0, swimmingAll.totalDistance().toMeters(), Constant.EPSILON);
        assertEquals(0L, swimmingAll.totalDuration().toSeconds());
        assertEquals(0.0, swimmingAll.maxDistance().toMeters(), Constant.EPSILON);
        assertEquals(0L, swimmingAll.maxDuration().toSeconds());
    }
}
