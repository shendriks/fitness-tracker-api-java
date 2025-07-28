package dev.shendriks.fitnesstrackerapi.domain.trophy.repository;

import dev.shendriks.fitnesstrackerapi.domain.achievement.entity.Achievement;
import dev.shendriks.fitnesstrackerapi.domain.trophy.entity.Trophy;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TrophyRepository extends CrudRepository<Trophy, Long> {
    Iterable<Trophy> findAllByUserOrderByCreatedAtDesc(User user);

    Optional<Trophy> findByUserAndAchievement(User user, Achievement achievement);
}
