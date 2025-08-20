package dev.shendriks.fitnesstrackerapi.application.port.out;

import dev.shendriks.fitnesstrackerapi.domain.entity.Trophy;
import dev.shendriks.fitnesstrackerapi.domain.value.AchievementId;
import dev.shendriks.fitnesstrackerapi.domain.value.AchievementUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

import java.util.List;

public interface ForAccessingTrophies {
    List<Trophy> findAllByUser(UserId userId);

    boolean existsByUserAndAchievement(UserId userId, AchievementId achievementId);

    Trophy createTrophyForUserAndAchievement(UserId userId, AchievementId achievementId);

    void deleteIfNotExistsByUserAndAchievement(UserId userId, AchievementId achievementId);

    void deleteIfNotExistsByUserAndAchievement(UserId userId, AchievementUlid achievementUlid);
}
