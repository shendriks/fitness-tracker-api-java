package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.TrophyDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrophyDbEntityRepository extends JpaRepository<TrophyDbEntity, Long> {
    List<TrophyDbEntity> findAllByUserIdOrderByUlidDesc(Long userId);

    List<TrophyDbEntity> user(UserDbEntity user);

    boolean existsByUserIdAndAchievementId(Long userId, Long achievementId);

    Optional<TrophyDbEntity> findByUserIdAndAchievementId(Long userId, Long achievementId);

    Optional<TrophyDbEntity> findByUserIdAndAchievementUlid(Long userId, String achievementUlid);
}
