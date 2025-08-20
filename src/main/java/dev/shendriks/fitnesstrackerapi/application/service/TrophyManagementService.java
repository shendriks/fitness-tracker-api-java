package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.event.TrophyLostEvent;
import dev.shendriks.fitnesstrackerapi.application.event.TrophyUnlockedEvent;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingTrophies;
import dev.shendriks.fitnesstrackerapi.domain.entity.Trophy;
import dev.shendriks.fitnesstrackerapi.domain.value.AchievementId;
import dev.shendriks.fitnesstrackerapi.domain.value.AchievementUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TrophyManagementService {
    private final ApplicationEventPublisher eventPublisher;
    private final ForAccessingTrophies forAccessingTrophies;

    public void createTrophyIfNotExists(UserId userId, AchievementId achievementId) {
        if (forAccessingTrophies.existsByUserAndAchievement(userId, achievementId)) {
            return;
        }
        Trophy trophy = forAccessingTrophies.createTrophyForUserAndAchievement(userId, achievementId);
        eventPublisher.publishEvent(new TrophyUnlockedEvent(this, userId, trophy.id()));
    }

    public void deleteTrophyIfExists(UserId userId, AchievementId achievementId) {
        forAccessingTrophies.deleteIfNotExistsByUserAndAchievement(userId, achievementId);
        eventPublisher.publishEvent(new TrophyLostEvent(this, userId));
    }

    public void deleteTrophyIfExists(UserId userId, AchievementUlid achievementUlid) {
        forAccessingTrophies.deleteIfNotExistsByUserAndAchievement(userId, achievementUlid);
        eventPublisher.publishEvent(new TrophyLostEvent(this, userId));
    }
}
