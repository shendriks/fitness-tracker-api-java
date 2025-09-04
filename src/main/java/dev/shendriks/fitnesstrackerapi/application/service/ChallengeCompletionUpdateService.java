package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.event.ChallengeBecameIncompleteEvent;
import dev.shendriks.fitnesstrackerapi.application.event.ChallengeCompletedEvent;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingChallengeParticipations;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAggregatingActivities;
import dev.shendriks.fitnesstrackerapi.domain.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.entity.ChallengeParticipation;
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
public class ChallengeCompletionUpdateService {
    private final ApplicationEventPublisher eventPublisher;
    private final ForAccessingChallengeParticipations forAccessingChallengeParticipations;
    private final ForAggregatingActivities forAggregatingActivities;
    private final AchievementCompletionCalculator achievementCompletionCalculator;
    private final TrophyManagementService trophyManagementService;

    public void updateAllChallengeCompletionsForUser(UserId userId) {
        List<ChallengeParticipation> challengeParticipations = forAccessingChallengeParticipations.findCurrentByUser(userId);

        for (ChallengeParticipation challengeParticipation : challengeParticipations) {
            updateChallengeCompletion(challengeParticipation);
        }
    }

    public void updateChallengeCompletion(ChallengeParticipation challengeParticipation) {
        Challenge challenge = challengeParticipation.challenge();
        UserId userId = challengeParticipation.userId();

        ActivityAggregationMap activityAggregationMap = forAggregatingActivities.aggregateForUserInTimeRange(
            userId,
            challenge.getStartDate(),
            challenge.getEndDate()
        );

        AchievementCompletionRequest request = new AchievementCompletionRequest(
            activityAggregationMap,
            challenge.getActivityType(),
            challenge.getActivityMetric(),
            challengeParticipation.percentageCompleted(),
            challenge.getCompletionThreshold()
        );

        AchievementCompletionResult result = achievementCompletionCalculator.calculateAchievementCompletion(request);

        forAccessingChallengeParticipations.updatePercentageCompleted(
            challengeParticipation.id(),
            result.percentageCompleted()
        );

        if (result.becameComplete()) {
            eventPublisher.publishEvent(new ChallengeCompletedEvent(userId, challenge.getId()));
            trophyManagementService.createTrophyIfNotExists(userId, challenge.getId());

        } else if (result.becameIncomplete()) {
            eventPublisher.publishEvent(new ChallengeBecameIncompleteEvent(userId, challenge.getId()));
            trophyManagementService.deleteTrophyIfExists(userId, challenge.getId());
        }
    }
}
