package dev.shendriks.fitnesstrackerapi.domain.challenge.service;

import dev.shendriks.fitnesstrackerapi.domain.achievement.service.AchievementProgressCalculator;
import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.activity.projection.ActivityAggregation;
import dev.shendriks.fitnesstrackerapi.domain.activity.projection.ActivityAggregationImpl;
import dev.shendriks.fitnesstrackerapi.domain.activity.service.ActivityStatsService;
import dev.shendriks.fitnesstrackerapi.domain.challenge.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.challenge.entity.ChallengeParticipation;
import dev.shendriks.fitnesstrackerapi.domain.challenge.event.ChallengeCompletedEvent;
import dev.shendriks.fitnesstrackerapi.domain.challenge.repository.ChallengeParticipationRepository;
import dev.shendriks.fitnesstrackerapi.domain.trophy.service.TrophyService;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import dev.shendriks.fitnesstrackerapi.supportive.TimeRange;
import lombok.extern.java.Log;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;

@Log
@Service
public class ChallengeCompletionChecker {
    private final ChallengeParticipationRepository challengeParticipationRepository;
    private final ActivityStatsService activityStatsService;
    private final TrophyService trophyService;
    private final AchievementProgressCalculator achievementProgressCalculator;
    private final ApplicationEventPublisher eventPublisher;

    public ChallengeCompletionChecker(
        ChallengeParticipationRepository challengeParticipationRepository,
        ActivityStatsService activityStatsService,
        TrophyService trophyService,
        AchievementProgressCalculator achievementProgressCalculator,
        ApplicationEventPublisher eventPublisher
    ) {
        this.challengeParticipationRepository = challengeParticipationRepository;
        this.activityStatsService = activityStatsService;
        this.trophyService = trophyService;
        this.achievementProgressCalculator = achievementProgressCalculator;
        this.eventPublisher = eventPublisher;
    }

    public void checkCompletionForUser(User user) {
        // 1. fetch all current unfinished challenges the user is participating in
        Iterable<ChallengeParticipation> challengeParticipations = challengeParticipationRepository.findCurrent(user, Instant.now());

        // 2. iterate over challenges
        for (ChallengeParticipation challengeParticipation : challengeParticipations) {
            boolean wasCompleted = challengeParticipation.isCompleted();

            // 3. for each challenge, compute activity stats. cache by time range if necessary
            Challenge challenge = challengeParticipation.getChallenge();

            TimeRange timeRange = TimeRange.of(challenge.getStartDate(), challenge.getEndDate());
            HashMap<ActivityType, ActivityAggregation> activityStatsByType = activityStatsService.aggregateActivitiesByType(user, timeRange);
            ActivityAggregation activityStats = activityStatsService.aggregateActivities(user, timeRange);

            // 4. evaluate progress
            int percentageCompleted;
            if (challenge.getActivityType() == null) {
                percentageCompleted = achievementProgressCalculator.calculateCompletionPercentage(challenge, activityStats);
            } else {
                ActivityAggregation activityStatsForType = activityStatsByType.getOrDefault(challenge.getActivityType(), new ActivityAggregationImpl());
                percentageCompleted = achievementProgressCalculator.calculateCompletionPercentage(challenge, activityStatsForType);
            }
            
            challengeParticipation.setPercentageCompleted(percentageCompleted);
            challengeParticipationRepository.save(challengeParticipation);

            boolean isCompleted = challengeParticipation.isCompleted();

            if (wasCompleted == isCompleted) {
                continue;
            }

            if (isCompleted) {
                // 5a. create trophy
                eventPublisher.publishEvent(new ChallengeCompletedEvent(this, user.getId(), challenge.getId()));
                trophyService.createTrophyIfNotExists(user, challenge);
                continue;
            }

            // 5b. trophy lost :-(
            trophyService.deleteTrophyIfExists(user, challenge);
        }
    }
}

