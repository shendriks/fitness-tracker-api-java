package dev.shendriks.fitnesstrackerapi.domain.achievement.eventlistener;

import dev.shendriks.fitnesstrackerapi.domain.achievement.entity.Achievement;
import dev.shendriks.fitnesstrackerapi.domain.achievement.repository.AchievementRepository;
import dev.shendriks.fitnesstrackerapi.domain.activity.event.ActivityUploadedEvent;
import dev.shendriks.fitnesstrackerapi.domain.activity.repository.ActivityRepository;
import dev.shendriks.fitnesstrackerapi.domain.challenge.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.challenge.repository.ChallengeRepository;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.user.repository.UserRepository;
import io.github.jamsesso.jsonlogic.JsonLogic;
import io.github.jamsesso.jsonlogic.JsonLogicException;
import lombok.Synchronized;
import lombok.extern.java.Log;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Log
@Component
public class AchievementChecker {
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final ChallengeRepository challengeRepository;
    private final AchievementRepository achievementRepository;

    public AchievementChecker(
        UserRepository userRepository,
        ActivityRepository activityRepository,
        ChallengeRepository challengeRepository,
        AchievementRepository achievementRepository
    ) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.challengeRepository = challengeRepository;
        this.achievementRepository = achievementRepository;
    }

    @EventListener
    @Synchronized
    @Async
    public void handleUploadedActivity(ActivityUploadedEvent event) {
        User user = userRepository.findById(event.getUserId()).orElseThrow();

        long activityCount = activityRepository.countByUser(user);
        Map<String, Long> challengeRuleData = new HashMap<>();
        challengeRuleData.put("ACTIVITY_COUNT", activityCount);

        List<Long> alreadyAchievedAchievementIds = user
            .getAchievements()
            .stream()
            .map(achievement -> achievement.getChallenge().getId())
            .toList();

        Iterable<Challenge> challenges = alreadyAchievedAchievementIds.isEmpty() 
            ? challengeRepository.findAll()
            : challengeRepository.findByIdNotIn(alreadyAchievedAchievementIds);
        
        for (var challenge : challenges) {
            JsonLogic jsonLogic = new JsonLogic();
            try {
                var isFulfilled = (boolean) jsonLogic.apply(challenge.getRuleJson(), challengeRuleData);
                if (isFulfilled) {
                    Achievement achievement = new Achievement();
                    achievement.setUser(user);
                    achievement.setChallenge(challenge);
                    achievement.setAchievedAt(Instant.now());
                    achievementRepository.save(achievement);
                }
            } catch (JsonLogicException | RuntimeException e) {
                log.severe("Failed to save achievement for challenge %s and user %s: %s".formatted(challenge.getName(), user.getEmail(), e.getMessage()));
            }
        }
    }
}
