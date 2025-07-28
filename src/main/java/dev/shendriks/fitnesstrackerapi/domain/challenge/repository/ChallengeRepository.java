package dev.shendriks.fitnesstrackerapi.domain.challenge.repository;

import dev.shendriks.fitnesstrackerapi.domain.challenge.entity.Challenge;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ChallengeRepository extends CrudRepository<Challenge, Long> {
    Iterable<Challenge> findByStartDateBeforeAndEndDateAfter(Instant startDateBefore, Instant endDateAfter);
    Iterable<Challenge> findByIdNotInAndStartDateBeforeAndEndDateAfter(List<Long> challengeIds, Instant startDateBefore, Instant endDateAfter);
    Optional<Challenge> findByUlid(String ulid);
    Iterable<Challenge> findAllByOrderByCreatedAtDesc();
}
