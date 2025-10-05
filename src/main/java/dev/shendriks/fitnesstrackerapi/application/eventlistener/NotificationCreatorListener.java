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
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Application event listener that creates user-facing notifications for various domain events
 * (activities, challenges, milestones, and trophies).
 */
@Component
@AllArgsConstructor
@Transactional
public class NotificationCreatorListener {
    private final ForAccessingNotifications forAccessingNotifications;
    private final ForAccessingChallenges forAccessingChallenges;
    private final ForAccessingMilestones forAccessingMilestones;
    private final ForAccessingActivities forAccessingActivities;

    /**
     * Creates a notification when a user joins a challenge.
     *
     * @param event the ChallengeJoinedEvent
     */
    @EventListener
    @Synchronized
    @Async
    @Order(0)
    public void handle(ChallengeJoinedEvent event) {
        String challengeName = forAccessingChallenges.findNameByUlid(event.challengeUlid()).orElseThrow();

        NotificationCreationData notification = NotificationCreationData
            .builder()
            .userId(event.userId())
            .title("Challenge joined")
            .description("You have joined the challenge \"" + challengeName + "\"")
            .build();
        forAccessingNotifications.save(notification);
    }

    /**
     * Creates a notification when a user leaves a challenge.
     *
     * @param event the ChallengeLeftEvent
     */
    @EventListener
    @Synchronized
    @Async
    @Order(0)
    public void handle(ChallengeLeftEvent event) {
        String challengeName = forAccessingChallenges.findNameByUlid(event.challengeUlid()).orElseThrow();
        NotificationCreationData notification = new NotificationCreationData(
            event.userId(),
            "Challenge left",
            "You have left the challenge \"" + challengeName + "\"");
        forAccessingNotifications.save(notification);
    }

    /**
     * Creates a notification when a user completes a challenge.
     *
     * @param event the ChallengeCompletedEvent
     */
    @EventListener
    @Synchronized
    @Async
    @Order(0)
    public void handle(ChallengeCompletedEvent event) {
        String challengeName = forAccessingChallenges.findNameById(event.challengeId()).orElseThrow();
        NotificationCreationData notification = new NotificationCreationData(
            event.userId(),
            "Wow, congratulations!",
            "You have completed the challenge " + challengeName
        );
        forAccessingNotifications.save(notification);
    }

    /**
     * Creates a notification when a user completes a milestone.
     *
     * @param event the MilestoneCompletedEvent
     */
    @EventListener
    @Synchronized
    @Async
    @Order(0)
    public void handle(MilestoneCompletedEvent event) {
        String milestoneName = forAccessingMilestones.findNameById(event.milestoneId()).orElseThrow();
        NotificationCreationData notification = new NotificationCreationData(
            event.userId(),
            "Another milestone reached!",
            "You have completed the milestone " + milestoneName
        );
        forAccessingNotifications.save(notification);
    }

    /**
     * Creates a notification when a user saves a new activity.
     *
     * @param event the ActivitySavedEvent
     */
    @EventListener
    @Synchronized
    @Async
    @Order(0)
    public void handle(ActivitySavedEvent event) {
        String activityTitle = forAccessingActivities.findTitleById(event.activityId()).orElseThrow();
        NotificationCreationData notification = new NotificationCreationData(
            event.userId(),
            "Well done!",
            "You have saved a new activity: " + activityTitle
        );
        forAccessingNotifications.save(notification);
    }

    /**
     * Creates a notification when a user updates an activity.
     *
     * @param event the ActivityUpdatedEvent
     */
    @EventListener
    @Synchronized
    @Async
    @Order(0)
    public void handle(ActivityUpdatedEvent event) {
        String activityTitle = forAccessingActivities.findTitleById(event.activityId()).orElseThrow();
        NotificationCreationData notification = new NotificationCreationData(
            event.userId(),
            "Activity updated!",
            "You have updated an activity: " + activityTitle
        );
        forAccessingNotifications.save(notification);
    }

    /**
     * Creates a notification when a user deletes an activity.
     *
     * @param event the ActivityDeletedEvent
     */
    @EventListener
    @Synchronized
    @Async
    @Order(0)
    public void handle(ActivityDeletedEvent event) {
        NotificationCreationData notification = new NotificationCreationData(
            event.userId(),
            "Bummer!",
            "You have deleted an activity"
        );
        forAccessingNotifications.save(notification);
    }

    /**
     * Creates a notification when a user unlocks a trophy.
     *
     * @param event the TrophyUnlockedEvent
     */
    @EventListener
    @Synchronized
    @Async
    @Order(0)
    public void handle(TrophyUnlockedEvent event) {
        NotificationCreationData notification = new NotificationCreationData(
            event.userId(),
            "Superb!",
            "You have unlocked a trophy"
        );
        forAccessingNotifications.save(notification);
    }

    /**
     * Creates a notification when a user loses a previously unlocked trophy.
     *
     * @param event the TrophyLostEvent
     */
    @EventListener
    @Synchronized
    @Async
    @Order(0)
    public void handle(TrophyLostEvent event) {
        NotificationCreationData notification = new NotificationCreationData(
            event.userId(),
            "D'oh!",
            "You have lost a trophy"
        );
        forAccessingNotifications.save(notification);
    }
}
