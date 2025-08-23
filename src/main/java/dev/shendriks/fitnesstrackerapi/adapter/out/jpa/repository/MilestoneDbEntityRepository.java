package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.MilestoneDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection.MilestoneProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MilestoneDbEntityRepository extends JpaRepository<MilestoneDbEntity, Long> {
    @Query("""
        SELECT
            m.id AS id,
            m.ulid AS ulid,
            a.name AS name,
            a.description AS description,
            a.imageFilePath AS imageFilePath,
            a.activityType AS activityType,
            a.activityMetric AS activityMetric,
            a.completionThreshold AS completionThreshold,
            CASE WHEN t.id IS NULL THEN false ELSE true END AS isCompleted
        FROM MilestoneDbEntity m
        JOIN AchievementDbEntity a ON (a.id = m.id)
        LEFT JOIN TrophyDbEntity t ON (
            t.achievement = m
            AND t.user.id = :userId
        )
        ORDER BY m.id ASC
        """)
    List<MilestoneProjection> findAllWithCompletedByUser(@Param("userId") Long userId);

    @Query("SELECT m.name FROM MilestoneDbEntity m WHERE m.id = :id")
    Optional<String> findNameById(@Param("id") Long id);
}
