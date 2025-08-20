package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.event.ChallengeBecameIncompleteEvent;
import dev.shendriks.fitnesstrackerapi.application.event.ChallengeCompletedEvent;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingChallengeParticipations;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAggregatingActivities;
import dev.shendriks.fitnesstrackerapi.domain.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.entity.ChallengeParticipation;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityAggregationMap;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Log
@Service
@AllArgsConstructor
public class ChallengeCompletionChecker {
    private final ApplicationEventPublisher eventPublisher;
    private final ForAccessingChallengeParticipations forAccessingChallengeParticipations;
    private final ForAggregatingActivities forAggregatingActivities;

    public void checkCompletionForUser(UserId userId) {
        List<ChallengeParticipation> challengeParticipations = forAccessingChallengeParticipations.findCurrentByUser(userId);

        for (ChallengeParticipation challengeParticipation : challengeParticipations) {
            updateChallengeCompletion(challengeParticipation);
        }
    }

    public void checkCompletion(UserId userId, ChallengeUlid challengeUlid) {
        ChallengeParticipation challengeParticipation = forAccessingChallengeParticipations
            .findByUserAndChallenge(userId, challengeUlid)
            .orElseThrow();
        updateChallengeCompletion(challengeParticipation);
    }

    private void updateChallengeCompletion(ChallengeParticipation challengeParticipation) {
        Challenge challenge = challengeParticipation.getChallenge();
        UserId userId = challengeParticipation.getUserId();
        // todo: cache based on time range
        ActivityAggregationMap activityAggregationMap = forAggregatingActivities.aggregateForUserByTypeInTimeRange(
            userId,
            challenge.getStartDate(),
            challenge.getEndDate()
        );

        challengeParticipation.updateCompletionPercentage(activityAggregationMap);

        forAccessingChallengeParticipations.updatePercentageCompleted(
            challengeParticipation.getId(),
            challengeParticipation.getPercentageCompleted()
        );

        if (challengeParticipation.isBecameComplete()) {
            eventPublisher.publishEvent(new ChallengeCompletedEvent(this, userId, challenge.getId()));
            return;
        }

        if (challengeParticipation.isBecameIncomplete()) {
            eventPublisher.publishEvent(new ChallengeBecameIncompleteEvent(this, userId, challenge.getId()));
        }
    }
}
