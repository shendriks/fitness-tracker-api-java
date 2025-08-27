package dev.shendriks.fitnesstrackerapi.application.eventlistener;

import dev.shendriks.fitnesstrackerapi.application.event.ActivityDeletedEvent;
import dev.shendriks.fitnesstrackerapi.application.event.ActivitySavedEvent;
import dev.shendriks.fitnesstrackerapi.application.event.ActivityUpdatedEvent;
import dev.shendriks.fitnesstrackerapi.application.service.ChallengeCompletionUpdateService;
import dev.shendriks.fitnesstrackerapi.application.service.MilestoneCompletionUpdateService;
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
    private final ChallengeCompletionUpdateService challengeCompletionUpdateService;
    private final MilestoneCompletionUpdateService milestoneCompletionUpdateService;

    @EventListener
    @Synchronized
    @Async
    public void handleSavedActivity(ActivitySavedEvent event) {
        milestoneCompletionUpdateService.updateAllMilestoneCompletionsForUser(event.userId());
        challengeCompletionUpdateService.updateAllChallengeCompletionsForUser(event.userId());
    }

    @EventListener
    @Synchronized
    @Async
    public void handleUpdatedActivity(ActivityUpdatedEvent event) {
        milestoneCompletionUpdateService.updateAllMilestoneCompletionsForUser(event.userId());
        challengeCompletionUpdateService.updateAllChallengeCompletionsForUser(event.userId());
    }

    @EventListener
    @Synchronized
    @Async
    public void handleDeletedActivity(ActivityDeletedEvent event) {
        milestoneCompletionUpdateService.updateAllMilestoneCompletionsForUser(event.userId());
        challengeCompletionUpdateService.updateAllChallengeCompletionsForUser(event.userId());
    }
}