package dev.shendriks.fitnesstrackerapi.domain.challenge.repository;

import dev.shendriks.fitnesstrackerapi.domain.activity.projection.ActivityAggregation;
import dev.shendriks.fitnesstrackerapi.domain.challenge.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.challenge.entity.ChallengeParticipation;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ChallengeParticipationRepository extends CrudRepository<ChallengeParticipation, Long> {
    Optional<ChallengeParticipation> findByChallengeAndUser(Challenge challenge, User user);

    boolean existsByChallengeAndUser(Challenge challenge, User user);

    Iterable<ChallengeParticipation> findByUser(User user);

    Iterable<ChallengeParticipation> findByUserAndChallengeStartDateBeforeAndChallengeEndDateAfter(User user, Instant now, Instant now1);

    Iterable<ChallengeParticipation> findByUserAndIdNotInAndChallengeStartDateBeforeAndChallengeEndDateAfter(User user, Collection<Long> alreadyAchievedAchievementsIds, Instant now, Instant now1);

    @Query("""
        SELECT cp
        FROM ChallengeParticipation cp
        JOIN Challenge c ON cp.challenge = c
        WHERE cp.user = :user
        AND c.startDate <= :now
        AND c.endDate > :now
        AND cp.percentageCompleted < 100
        """)
    Iterable<ChallengeParticipation> findUnfinished(
        @Param("user") User user,
        @Param("now") Instant now
        );
    
//    @Query("""
//        SELECT cp
//        FROM ChallengeParticipation cp
//        JOIN Challenge c ON cp.challenge = c
//        WHERE cp.user = :user
//        AND c.startDate <= :now
//        AND c.endDate > :now
//        AND cp.percentageCompleted >= 100
//        """)
//    Iterable<ChallengeParticipation> findCompleted(
//        @Param("user") User user,
//        @Param("now") Instant now
//        );
}
