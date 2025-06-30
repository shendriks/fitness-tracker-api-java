package dev.shendriks.fitnesstrackerapi.domain.challenge.repository;

import dev.shendriks.fitnesstrackerapi.domain.challenge.entity.Challenge;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChallengeRepository extends CrudRepository<Challenge, Long> {
    Iterable<Challenge> findByIdNotIn(List<Long> challengeIds);
}
