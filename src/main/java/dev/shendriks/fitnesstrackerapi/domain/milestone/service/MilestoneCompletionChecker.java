package dev.shendriks.fitnesstrackerapi.domain.milestone.service;

import dev.shendriks.fitnesstrackerapi.domain.achievement.service.AchievementProgressCalculator;
import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.activity.projection.ActivityAggregation;
import dev.shendriks.fitnesstrackerapi.domain.activity.projection.ActivityAggregationImpl;
import dev.shendriks.fitnesstrackerapi.domain.activity.service.ActivityStatsService;
import dev.shendriks.fitnesstrackerapi.domain.milestone.entity.Milestone;
import dev.shendriks.fitnesstrackerapi.domain.milestone.event.MilestoneCompletedEvent;
import dev.shendriks.fitnesstrackerapi.domain.milestone.repository.MilestoneRepository;
import dev.shendriks.fitnesstrackerapi.domain.trophy.service.TrophyService;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import lombok.extern.java.Log;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Log
@Service
public class MilestoneCompletionChecker {
    private final MilestoneRepository milestoneRepository;
    private final ActivityStatsService activityStatsService;
    private final TrophyService trophyService;
    private final AchievementProgressCalculator achievementProgressCalculator;
    private final ApplicationEventPublisher eventPublisher;

    public MilestoneCompletionChecker(
        MilestoneRepository milestoneRepository,
        ActivityStatsService activityStatsService,
        TrophyService trophyService,
        AchievementProgressCalculator achievementProgressCalculator,
        ApplicationEventPublisher eventPublisher
    ) {
        this.milestoneRepository = milestoneRepository;
        this.activityStatsService = activityStatsService;
        this.trophyService = trophyService;
        this.achievementProgressCalculator = achievementProgressCalculator;
        this.eventPublisher = eventPublisher;
    }

    public void checkCompletionForUser(User user) {
//        List<Long> completedMilestoneIds = user
//            .getTrophies()
//            .stream()
//            .map(Trophy::getAchievement)
//            .filter(achievement -> achievement instanceof Milestone)
//            .map(Achievement::getId)
//            .toList();
        
        HashMap<ActivityType, ActivityAggregation> activityStatsByType = activityStatsService.aggregateActivitiesByType(user);
        ActivityAggregation activityStats = activityStatsService.aggregateActivities(user);
        
        Iterable<Milestone> incompleteMilestones = milestoneRepository.findIncompleteByUser(user);
        for (Milestone milestone : incompleteMilestones) {
            int progress;
            if (milestone.getActivityType() == null) {
                progress = achievementProgressCalculator.calculateCompletionPercentage(milestone, activityStats);
            } else {
                ActivityAggregation activityStatsForType = activityStatsByType.getOrDefault(milestone.getActivityType(), new ActivityAggregationImpl());
                progress = achievementProgressCalculator.calculateCompletionPercentage(milestone, activityStatsForType);
            }

            if (progress >= 100) {
                eventPublisher.publishEvent(new MilestoneCompletedEvent(this, user.getId(), milestone.getId()));
                trophyService.createTrophyIfNotExists(user, milestone);
            }
        }

        Iterable<Milestone> completeMilestones = milestoneRepository.findCompletedByUser(user);
        for (Milestone milestone : completeMilestones) {
            int progress;
            if (milestone.getActivityType() == null) {
                progress = achievementProgressCalculator.calculateCompletionPercentage(milestone, activityStats);
            } else {
                ActivityAggregation activityStatsForType = activityStatsByType.getOrDefault(milestone.getActivityType(), new ActivityAggregationImpl());
                progress = achievementProgressCalculator.calculateCompletionPercentage(milestone, activityStatsForType);
            }

            if (progress < 100) {
                trophyService.deleteTrophyIfExists(user, milestone);
            }
        }
    }
}

