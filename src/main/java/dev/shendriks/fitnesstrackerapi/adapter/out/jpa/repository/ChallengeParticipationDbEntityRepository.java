package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ChallengeDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ChallengeParticipationDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ChallengeParticipationDbEntityRepository extends JpaRepository<ChallengeParticipationDbEntity, Long> {
    Optional<ChallengeParticipationDbEntity> findByChallengeAndUser(ChallengeDbEntity challenge, UserDbEntity user);

    boolean existsByChallengeUlidAndUserId(String challengeUlid, Long userId);

    @Query("""
        SELECT cp
        FROM ChallengeParticipationDbEntity cp
        JOIN ChallengeDbEntity c ON cp.challenge = c
        WHERE cp.user.id = :userId
        AND c.startDate <= :now
        AND c.endDate > :now
        ORDER BY c.id
        """)
    List<ChallengeParticipationDbEntity> findCurrent(
        @Param("userId") Long userId,
        @Param("now") Instant now
    );

    @Query("""
        SELECT cp
        FROM ChallengeParticipationDbEntity cp
        JOIN ChallengeDbEntity c ON cp.challenge = c
        WHERE cp.user.id = :userId AND c.ulid = :challengeUlid
        """)
    Optional<ChallengeParticipationDbEntity> findByUserIdAndChallengeUlid(
        @Param("userId") Long userId,
        @Param("challengeUlid") String challengeUlid
    );
}
