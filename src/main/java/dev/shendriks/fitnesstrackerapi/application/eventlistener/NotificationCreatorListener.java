package dev.shendriks.fitnesstrackerapi.application.eventlistener;

import dev.shendriks.fitnesstrackerapi.application.event.*;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingActivities;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingChallenges;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingMilestones;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingNotifications;
import dev.shendriks.fitnesstrackerapi.domain.value.NotificationCreationData;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Transactional
public class NotificationCreatorListener {
    private final ForAccessingNotifications forAccessingNotifications;
    private final ForAccessingChallenges forAccessingChallenges;
    private final ForAccessingMilestones forAccessingMilestones;
    private final ForAccessingActivities forAccessingActivities;

    @EventListener
    @Synchronized
    @Async
    public void handleChallengeJoinedEvent(ChallengeJoinedEvent event) {
        String challengeName = forAccessingChallenges.findNameByUlid(event.challengeUlid()).orElseThrow();

        NotificationCreationData notification = NotificationCreationData
            .builder()
            .userId(event.userId())
            .title("Challenge joined")
            .description("You have joined the challenge \"" + challengeName + "\"")
            .build();
        forAccessingNotifications.save(notification);
    }

    @EventListener
    @Synchronized
    @Async
    public void handleChallengeLeftEvent(ChallengeLeftEvent event) {
        String challengeName = forAccessingChallenges.findNameByUlid(event.challengeUlid()).orElseThrow();
        NotificationCreationData notification = new NotificationCreationData(
            event.userId(),
            "Challenge left",
            "You have left the challenge \"" + challengeName + "\"");
        forAccessingNotifications.save(notification);
    }

    @EventListener
    @Synchronized
    @Async
    public void handleChallengeCompletedEvent(ChallengeCompletedEvent event) {
        String challengeName = forAccessingChallenges.findNameById(event.challengeId()).orElseThrow();
        NotificationCreationData notification = new NotificationCreationData(
            event.userId(),
            "Wow, congratulations!",
            "You have completed the challenge " + challengeName
        );
        forAccessingNotifications.save(notification);
    }

    @EventListener
    @Synchronized
    @Async
    public void handleMilestoneCompletedEvent(MilestoneCompletedEvent event) {
        String milestoneName = forAccessingMilestones.findNameById(event.milestoneId()).orElseThrow();
        NotificationCreationData notification = new NotificationCreationData(
            event.userId(),
            "Another milestone reached!",
            "You have completed the milestone " + milestoneName
        );
        forAccessingNotifications.save(notification);
    }

    @EventListener
    @Synchronized
    @Async
    public void handleActivitySavedEvent(ActivitySavedEvent event) {
        String activityTitle = forAccessingActivities.findTitleById(event.activityId()).orElseThrow();
        NotificationCreationData notification = new NotificationCreationData(
            event.userId(),
            "Well done!",
            "You have saved a new activity: " + activityTitle
        );
        forAccessingNotifications.save(notification);
    }

    @EventListener
    @Synchronized
    @Async
    public void handleActivityUpdatedEvent(ActivityUpdatedEvent event) {
        String activityTitle = forAccessingActivities.findTitleById(event.activityId()).orElseThrow();
        NotificationCreationData notification = new NotificationCreationData(
            event.userId(),
            "Activity updated!",
            "You have updated an activity: " + activityTitle
        );
        forAccessingNotifications.save(notification);
    }

    @EventListener
    @Synchronized
    @Async
    public void handleActivityDeletedEvent(ActivityDeletedEvent event) {
        NotificationCreationData notification = new NotificationCreationData(
            event.userId(),
            "Bummer!",
            "You have deleted an activity"
        );
        forAccessingNotifications.save(notification);
    }

    @EventListener
    @Synchronized
    @Async
    public void handleTrophyUnlockedEvent(TrophyUnlockedEvent event) {
        NotificationCreationData notification = new NotificationCreationData(
            event.userId(),
            "Superb!",
            "You have unlocked a trophy"
        );
        forAccessingNotifications.save(notification);
    }

    @EventListener
    @Synchronized
    @Async
    public void handleTrophyLostEvent(TrophyLostEvent event) {
        NotificationCreationData notification = new NotificationCreationData(
            event.userId(),
            "D'oh!",
            "You have lost a trophy"
        );
        forAccessingNotifications.save(notification);
    }
}
