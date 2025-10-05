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

/**
 * Application event listener that reacts to activity lifecycle events to refresh
 * milestone and challenge completion for the affected user.
 */
@Component
@AllArgsConstructor
@Transactional
public class ActivityListener {
    private final ChallengeCompletionUpdateService challengeCompletionUpdateService;
    private final MilestoneCompletionUpdateService milestoneCompletionUpdateService;

    /**
     * Handles ActivitySavedEvent by recalculating milestone and challenge completions for the user.
     *
     * @param event the activity saved event containing the user context
     */
    @EventListener
    @Synchronized
    @Async
    public void handleSavedActivity(ActivitySavedEvent event) {
        milestoneCompletionUpdateService.updateAllMilestoneCompletionsForUser(event.userId());
        challengeCompletionUpdateService.updateAllChallengeCompletionsForUser(event.userId());
    }

    /**
     * Handles ActivityUpdatedEvent by recalculating milestone and challenge completions for the user.
     *
     * @param event the activity updated event containing the user context
     */
    @EventListener
    @Synchronized
    @Async
    public void handleUpdatedActivity(ActivityUpdatedEvent event) {
        milestoneCompletionUpdateService.updateAllMilestoneCompletionsForUser(event.userId());
        challengeCompletionUpdateService.updateAllChallengeCompletionsForUser(event.userId());
    }

    /**
     * Handles ActivityDeletedEvent by recalculating milestone and challenge completions for the user.
     *
     * @param event the activity deleted event containing the user context
     */
    @EventListener
    @Synchronized
    @Async
    public void handleDeletedActivity(ActivityDeletedEvent event) {
        milestoneCompletionUpdateService.updateAllMilestoneCompletionsForUser(event.userId());
        challengeCompletionUpdateService.updateAllChallengeCompletionsForUser(event.userId());
    }
}