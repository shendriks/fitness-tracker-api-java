package dev.shendriks.fitnesstrackerapi.domain.achievement.repository;

import dev.shendriks.fitnesstrackerapi.domain.achievement.entity.Achievement;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface AchievementRepository extends CrudRepository<Achievement, Long> {
    Iterable<Achievement> findAllByUser(User user);
}
