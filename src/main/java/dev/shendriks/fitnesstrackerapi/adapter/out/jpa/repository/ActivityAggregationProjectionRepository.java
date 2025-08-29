package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ActivityDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection.ActivityAggregationDbProjection;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection.ActivityTypeAggregationDbProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ActivityAggregationProjectionRepository extends JpaRepository<ActivityDbEntity, Long> {
    @NativeQuery("""
        SELECT
            activity_type AS type,
            COUNT(id) AS count,
            CAST(COALESCE(SUM(distance), 0) AS DOUBLE) AS totalDistance,
            CAST(COALESCE(SUM(duration), 0) AS DOUBLE) AS totalDuration,
            COALESCE(MAX(distance), 0) AS maxDistance,
            COALESCE(MAX(duration), 0) AS maxDuration
        FROM activity
        WHERE user_id = :userId
        AND (:from IS NULL OR start_date >= :from)
        AND (:to IS NULL OR start_date < :to)
        GROUP BY activity_type
        """)
//    @Query("""
//        SELECT
//            activityType AS type,
//            COUNT(id) AS count,
//            CAST(COALESCE(SUM(CAST(distance AS DOUBLE)), 0) AS DOUBLE) AS totalDistance,
//            CAST(COALESCE(SUM(CAST(duration AS DOUBLE)), 0) AS DOUBLE) AS totalDuration,
//            CAST(COALESCE(MAX(distance), 0) AS DOUBLE) AS maxDistance,
//            CAST(COALESCE(MAX(duration), 0) AS DOUBLE) AS maxDuration
//        FROM ActivityDbEntity
//        WHERE user.id = :userId
//        AND (:from IS NULL OR startDate >= :from)
//        AND (:to IS NULL OR startDate < :to)
//        GROUP BY activityType
//        """)
    List<ActivityTypeAggregationDbProjection> aggregateForUserByTypeInTimeRange(
        @Param("userId") Long userId,
        @Param("from") Instant from,
        @Param("to") Instant to
    );

    @NativeQuery("""
        SELECT
            COUNT(id) AS count,
            CAST(COALESCE(SUM(distance), 0) AS DOUBLE) AS totalDistance,
            CAST(COALESCE(SUM(duration), 0) AS DOUBLE) AS totalDuration,
            COALESCE(MAX(distance), 0) AS maxDistance,
            COALESCE(MAX(duration), 0) AS maxDuration
        FROM activity
        WHERE user_id = :userId
        AND (:from IS NULL OR start_date >= :from)
        AND (:to IS NULL OR start_date < :to)
        """)
//    @Query("""
//        SELECT
//            COUNT(id) AS count,
//            CAST(COALESCE(SUM(CAST(distance AS DOUBLE)), 0) AS DOUBLE) AS totalDistance ,
//            CAST(COALESCE(SUM(CAST(duration AS DOUBLE)), 0) AS DOUBLE) AS totalDuration,
//            CAST(COALESCE(MAX(distance), 0) AS DOUBLE) AS maxDistance,
//            CAST(COALESCE(MAX(duration), 0) AS DOUBLE) AS maxDuration
//        FROM ActivityDbEntity
//        WHERE user.id = :userId
//        AND (:from IS NULL OR startDate >= :from)
//        AND (:to IS NULL OR startDate < :to)
//        """)
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
