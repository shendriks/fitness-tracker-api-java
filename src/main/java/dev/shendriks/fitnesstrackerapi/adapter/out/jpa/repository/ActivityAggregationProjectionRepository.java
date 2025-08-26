package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ActivityDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection.ActivityAggregationDbProjection;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection.ActivityTypeAggregationDbProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ActivityAggregationProjectionRepository extends JpaRepository<ActivityDbEntity, Long> {
    @Query("""
        SELECT
            activityType AS type,
            COUNT(id) AS count,
            COALESCE(SUM(distance), 0) AS totalDistance,
            COALESCE(SUM(duration), 0) AS totalDuration,
            COALESCE(MAX(distance), 0) AS maxDistance,
            COALESCE(MAX(duration), 0) AS maxDuration
        FROM ActivityDbEntity
        WHERE user.id = :userId
        AND (:from IS NULL OR startDate >= :from)
        AND (:to IS NULL OR startDate < :to)
        GROUP BY activityType
        """)
    List<ActivityTypeAggregationDbProjection> aggregateForUserByTypeInTimeRange(
        @Param("userId") Long userId,
        @Param("from") Instant from,
        @Param("to") Instant to
    );

    @Query("""
        SELECT
            COUNT(id) AS count,
            COALESCE(SUM(distance), 0) AS totalDistance,
            COALESCE(SUM(duration), 0) AS totalDuration,
            COALESCE(MAX(distance), 0) AS maxDistance,
            COALESCE(MAX(duration), 0) AS maxDuration
        FROM ActivityDbEntity
        WHERE user.id = :userId
        AND (:from IS NULL OR startDate >= :from)
        AND (:to IS NULL OR startDate < :to)
        """)
    ActivityAggregationDbProjection aggregateForUserInTimeRange(
        @Param("userId") Long userId,
        @Param("from") Instant from,
        @Param("to") Instant to
    );

    default List<ActivityTypeAggregationDbProjection> aggregateForUserByType(@Param("userId") Long userId) {
        return aggregateForUserByTypeInTimeRange(userId, null, null);
    }

    default ActivityAggregationDbProjection aggregateForUser(@Param("userId") Long userId) {
        return aggregateForUserInTimeRange(userId, null, null);
    }
}
