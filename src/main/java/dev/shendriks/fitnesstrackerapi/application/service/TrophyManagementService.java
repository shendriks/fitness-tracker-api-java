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

/**
 * Application service coordinating trophy creation and deletion based on achievement state.
 */
@Service
@AllArgsConstructor
public class TrophyManagementService {
    private final ApplicationEventPublisher eventPublisher;
    private final ForAccessingTrophies forAccessingTrophies;

    /**
         * Creates a trophy for the user and achievement if it does not already exist.
         * Publishes a TrophyUnlockedEvent when created.
         *
         * @param userId the user identifier
         * @param achievementId the achievement identifier
         */
        public void createTrophyIfNotExists(UserId userId, AchievementId achievementId) {
        if (forAccessingTrophies.existsByUserAndAchievement(userId, achievementId)) {
            return;
        }
        Trophy trophy = forAccessingTrophies.createTrophyForUserAndAchievement(userId, achievementId);
        eventPublisher.publishEvent(new TrophyUnlockedEvent(userId, trophy.id()));
    }

    /**
         * Deletes the trophy for the given user and achievement if it exists.
         * Publishes a TrophyLostEvent when removed.
         *
         * @param userId the user identifier
         * @param achievementId the achievement identifier
         */
        public void deleteTrophyIfExists(UserId userId, AchievementId achievementId) {
        forAccessingTrophies.deleteIfNotExistsByUserAndAchievement(userId, achievementId);
        eventPublisher.publishEvent(new TrophyLostEvent(userId));
    }

    /**
         * Deletes the trophy for the given user and achievement ULID if it exists.
         * Publishes a TrophyLostEvent when removed.
         *
         * @param userId the user identifier
         * @param achievementUlid the achievement ULID
         */
        public void deleteTrophyIfExists(UserId userId, AchievementUlid achievementUlid) {
        forAccessingTrophies.deleteIfNotExistsByUserAndAchievement(userId, achievementUlid);
        eventPublisher.publishEvent(new TrophyLostEvent(userId));
    }
}
