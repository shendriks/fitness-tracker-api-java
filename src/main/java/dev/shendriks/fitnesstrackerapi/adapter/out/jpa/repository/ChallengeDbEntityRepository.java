package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ChallengeDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection.ChallengeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ChallengeDbEntityRepository extends JpaRepository<ChallengeDbEntity, Long> {
    Optional<ChallengeDbEntity> findByUlid(String ulid);

    @Query("""
            SELECT
                c.id AS id,
                c.ulid AS ulid,
                c.name AS name,
                c.description AS description,
                c.imageFilePath AS imageFilePath,
                c.activityType AS activityType,
                c.activityMetric AS activityMetric,
                c.completionThreshold AS completionThreshold,
                c.startDate AS startDate,
                c.endDate AS endDate,
                CASE WHEN cp.id IS NULL THEN false ELSE true END AS hasUserJoined
            FROM ChallengeDbEntity c
            LEFT JOIN ChallengeParticipationDbEntity cp ON (
                cp.challenge = c
                AND cp.user.id = :userId
            )
            WHERE c.ulid = :ulid
        """)
    Optional<ChallengeProjection> findByUserIdAndUlidWithUserJoined(
        @Param("userId") Long userId,
        @Param("ulid") String ulid
    );

    @Query("""
            SELECT
                c.id AS id,
                c.ulid AS ulid,
                c.name AS name,
                c.description AS description,
                c.imageFilePath AS imageFilePath,
                c.activityType AS activityType,
                c.activityMetric AS activityMetric,
                c.completionThreshold AS completionThreshold,
                c.startDate AS startDate,
                c.endDate AS endDate,
                CASE WHEN cp.id IS NULL THEN false ELSE true END AS hasUserJoined
            FROM ChallengeDbEntity c
            LEFT JOIN ChallengeParticipationDbEntity cp ON (
                cp.challenge = c
                AND cp.user.id = :userId
            )
            WHERE (
                c.startDate <= :now
                AND c.endDate > :now
            )
            ORDER BY c.id
        """)
    List<ChallengeProjection> findAllCurrentWithUserJoined(
        @Param("userId") Long userId,
        @Param("now") Instant now
    );

    @Query("SELECT c.name FROM ChallengeDbEntity c WHERE c.id = :id")
    Optional<String> findNameById(@Param("id") Long id);

    @Query("SELECT c.name FROM ChallengeDbEntity c WHERE c.ulid = :ulid")
    Optional<String> findNameByUlid(@Param("ulid") String ulid);

    @Query("SELECT COUNT(c) > 0 FROM ChallengeDbEntity c WHERE c.ulid = :ulid")
    boolean existsByUlid(@Param("ulid") String ulid);
}
