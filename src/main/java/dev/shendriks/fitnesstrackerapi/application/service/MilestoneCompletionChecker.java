package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.event.MilestoneBecameIncompleteEvent;
import dev.shendriks.fitnesstrackerapi.application.event.MilestoneCompletedEvent;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingMilestones;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAggregatingActivities;
import dev.shendriks.fitnesstrackerapi.domain.entity.Milestone;
import dev.shendriks.fitnesstrackerapi.domain.service.AchievementCompletionCalculator;
import dev.shendriks.fitnesstrackerapi.domain.value.AchievementCompletionRequest;
import dev.shendriks.fitnesstrackerapi.domain.value.AchievementCompletionResult;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityAggregationMap;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Log
@Service
@AllArgsConstructor
public class MilestoneCompletionChecker {
    private final ApplicationEventPublisher eventPublisher;
    private final ForAccessingMilestones forAccessingMilestones;
    private final ForAggregatingActivities forAggregatingActivities;
    private final AchievementCompletionCalculator achievementCompletionCalculator;
    private final TrophyManagementService trophyManagementService;

    public void checkCompletionForUser(UserId userId) {
        List<Milestone> milestones = forAccessingMilestones.findAllWithCompletedByUser(userId);
        ActivityAggregationMap activityAggregationMap = forAggregatingActivities.aggregateForUserByType(userId);

        for (Milestone milestone : milestones) {
            AchievementCompletionRequest request = new AchievementCompletionRequest(
                activityAggregationMap,
                milestone.getActivityType(),
                milestone.getActivityMetric(),
                milestone.isCompleted() ? 100 : 0,
                milestone.getCompletionThreshold()
            );

            AchievementCompletionResult result = achievementCompletionCalculator.calculateAchievementCompletion(request);

            if (result.becameComplete()) {
                eventPublisher.publishEvent(new MilestoneCompletedEvent(this, userId, milestone.getId()));
                trophyManagementService.createTrophyIfNotExists(userId, milestone.getId());
            } else if (result.becameIncomplete()) {
                eventPublisher.publishEvent(new MilestoneBecameIncompleteEvent(this, userId, milestone.getId()));
                trophyManagementService.deleteTrophyIfExists(userId, milestone.getId());
            }
        }
    }
}

