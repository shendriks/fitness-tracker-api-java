package dev.shendriks.fitnesstrackerapi.domain.achievement.repository;

import dev.shendriks.fitnesstrackerapi.domain.achievement.entity.Achievement;
import dev.shendriks.fitnesstrackerapi.domain.challenge.entity.Challenge;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AchievementRepository extends CrudRepository<Achievement, Long> {
    Iterable<Challenge> findByIdNotIn(List<Long> achievementIds);
}
