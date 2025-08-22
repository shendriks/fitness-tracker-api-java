package dev.shendriks.fitnesstrackerapi.application.eventlistener;

import dev.shendriks.fitnesstrackerapi.application.event.ActivityDeletedEvent;
import dev.shendriks.fitnesstrackerapi.application.event.ActivitySavedEvent;
import dev.shendriks.fitnesstrackerapi.application.event.ActivityUpdatedEvent;
import dev.shendriks.fitnesstrackerapi.application.service.ChallengeCompletionChecker;
import dev.shendriks.fitnesstrackerapi.application.service.MilestoneCompletionChecker;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Transactional
public class ActivityListener {
    private final ChallengeCompletionChecker challengeCompletionChecker;
    private final MilestoneCompletionChecker milestoneCompletionChecker;

    @EventListener
    @Synchronized
    @Async
    public void handleSavedActivity(ActivitySavedEvent event) {
        milestoneCompletionChecker.checkCompletionForUser(event.getUserId());
        challengeCompletionChecker.checkCompletionForUser(event.getUserId());
    }

    @EventListener
    @Synchronized
    @Async
    public void handleUpdatedActivity(ActivityUpdatedEvent event) {
        milestoneCompletionChecker.checkCompletionForUser(event.getUserId());
        challengeCompletionChecker.checkCompletionForUser(event.getUserId());
    }

    @EventListener
    @Synchronized
    @Async
    public void handleDeletedActivity(ActivityDeletedEvent event) {
        milestoneCompletionChecker.checkCompletionForUser(event.getUserId());
        challengeCompletionChecker.checkCompletionForUser(event.getUserId());
    }
}