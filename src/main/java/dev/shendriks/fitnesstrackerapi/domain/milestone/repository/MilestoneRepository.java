package dev.shendriks.fitnesstrackerapi.domain.milestone.repository;

import dev.shendriks.fitnesstrackerapi.domain.milestone.entity.Milestone;
import dev.shendriks.fitnesstrackerapi.domain.milestone.projection.MilestoneProjection;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MilestoneRepository extends CrudRepository<Milestone, Long> {
    Iterable<Milestone> findByIdNotIn(List<Long> milestoneIds);

    @Query("""
        SELECT
            m.ulid AS ulid,
            a.name AS name,
            a.description AS description,
            a.imageFilePath AS imageFilePath,
            CASE WHEN t.id IS NULL THEN false ELSE true END AS completed
        FROM Milestone m
        JOIN Achievement a ON (a.id = m.id)
        LEFT JOIN Trophy t ON (
            t.achievement = m
            AND t.user = :user
        )
        ORDER BY m.id ASC
        """)
    List<MilestoneProjection> findAllWithCompletedByUser(@Param("user") User user);
}
