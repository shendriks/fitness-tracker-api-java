package dev.shendriks.fitnesstrackerapi.domain.activity.repository;

import dev.shendriks.fitnesstrackerapi.domain.activity.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.activity.projection.ActivityAggregation;
import dev.shendriks.fitnesstrackerapi.domain.activity.projection.ActivityTypeAggregation;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends
    CrudRepository<Activity, Long>,
    PagingAndSortingRepository<Activity, Long> {
    Iterable<Activity> findAllByUserOrderByUlidDesc(User user);

    long countByUser(User user);

    Optional<Activity> findByUserAndUlid(User user, String id);

    Iterable<Activity> findByStartDateBetween(Instant startDateAfter, Instant startDateBefore);

    Iterable<Activity> findByUserAndStartDateBetween(User user, Instant startDateAfter, Instant startDateBefore);

    @Query("""
        SELECT
                activityType AS type,
                COUNT(id) AS count,
                SUM(distance) AS totalDistance,
                SUM(duration) AS totalDuration,
                MAX(distance) AS maxDistance,
                MAX(duration) AS maxDuration
        FROM Activity
        WHERE user = :user
        AND (:from IS NULL OR startDate >= :from)
        AND (:to IS NULL OR startDate < :to)
        GROUP BY activityType
        """)
    List<ActivityTypeAggregation> aggregateByType(
        @Param("user") User user,
        @Param("from") Instant from,
        @Param("to") Instant to
    );

    @Query("""
        SELECT
                COUNT(id) AS count,
                SUM(distance) AS totalDistance,
                SUM(duration) AS totalDuration,
                MAX(distance) AS maxDistance,
                MAX(duration) AS maxDuration
        FROM Activity
        WHERE user = :user
        AND (:from IS NULL OR startDate >= :from)
        AND (:to IS NULL OR startDate < :to)
        """)
    ActivityAggregation aggregate(
        @Param("user") User user,
        @Param("from") Instant from,
        @Param("to") Instant to
    );
}
