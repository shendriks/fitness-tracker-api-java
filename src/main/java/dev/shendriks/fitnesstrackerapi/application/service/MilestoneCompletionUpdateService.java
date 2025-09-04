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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MilestoneCompletionUpdateService {
    private final ApplicationEventPublisher eventPublisher;
    private final ForAccessingMilestones forAccessingMilestones;
    private final ForAggregatingActivities forAggregatingActivities;
    private final AchievementCompletionCalculator achievementCompletionCalculator;
    private final TrophyManagementService trophyManagementService;

    public void updateAllMilestoneCompletionsForUser(UserId userId) {
        List<Milestone> milestones = forAccessingMilestones.findAllWithCompletedByUser(userId);
        ActivityAggregationMap activityAggregationMap = forAggregatingActivities.aggregateForUser(userId);

        for (Milestone milestone : milestones) {
            AchievementCompletionRequest request = AchievementCompletionRequest
                .builder()
                .activityAggregationMap(activityAggregationMap)
                .activityType(milestone.getActivityType())
                .activityMetric(milestone.getActivityMetric())
                .currentPercentageCompleted(milestone.isCompleted() ? 100 : 0)
                .completionThreshold(milestone.getCompletionThreshold())
                .build();

            AchievementCompletionResult result = achievementCompletionCalculator.calculateAchievementCompletion(request);

            if (result.becameComplete()) {
                eventPublisher.publishEvent(new MilestoneCompletedEvent(userId, milestone.getId()));
                trophyManagementService.createTrophyIfNotExists(userId, milestone.getId());
            } else if (result.becameIncomplete()) {
                eventPublisher.publishEvent(new MilestoneBecameIncompleteEvent(userId, milestone.getId()));
                trophyManagementService.deleteTrophyIfExists(userId, milestone.getId());
            }
        }
    }
}

