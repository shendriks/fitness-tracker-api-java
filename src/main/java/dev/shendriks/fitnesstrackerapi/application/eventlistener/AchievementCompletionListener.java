package dev.shendriks.fitnesstrackerapi.application.eventlistener;

import dev.shendriks.fitnesstrackerapi.application.event.ChallengeBecameIncompleteEvent;
import dev.shendriks.fitnesstrackerapi.application.event.ChallengeCompletedEvent;
import dev.shendriks.fitnesstrackerapi.application.event.MilestoneBecameIncompleteEvent;
import dev.shendriks.fitnesstrackerapi.application.event.MilestoneCompletedEvent;
import dev.shendriks.fitnesstrackerapi.application.service.TrophyManagementService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Transactional
@AllArgsConstructor
public class AchievementCompletionListener {
    private final TrophyManagementService trophyManagementService;

    @EventListener
    @Synchronized
    public void handle(ChallengeCompletedEvent event) {
        trophyManagementService.createTrophyIfNotExists(event.getUserId(), event.getChallengeId());
    }

    @EventListener
    @Synchronized
    public void handle(ChallengeBecameIncompleteEvent event) {
        trophyManagementService.deleteTrophyIfExists(event.getUserId(), event.getChallengeId());
    }

    @EventListener
    @Synchronized
    public void handle(MilestoneCompletedEvent event) {
        trophyManagementService.createTrophyIfNotExists(event.getUserId(), event.getMilestoneId());
    }

    @EventListener
    @Synchronized
    public void handle(MilestoneBecameIncompleteEvent event) {
        trophyManagementService.deleteTrophyIfExists(event.getUserId(), event.getMilestoneId());
    }
}
