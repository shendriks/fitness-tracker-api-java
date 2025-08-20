package dev.shendriks.fitnesstrackerapi.application.eventlistener;

import dev.shendriks.fitnesstrackerapi.application.event.ChallengeJoinedEvent;
import dev.shendriks.fitnesstrackerapi.application.event.ChallengeLeftEvent;
import dev.shendriks.fitnesstrackerapi.application.service.ChallengeCompletionChecker;
import dev.shendriks.fitnesstrackerapi.application.service.TrophyManagementService;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ChallengeJoinLeaveListener {
    private final ChallengeCompletionChecker challengeCompletionChecker;
    private final TrophyManagementService trophyManagementService;

    @EventListener
    @Synchronized
    public void handleChallengeJoined(ChallengeJoinedEvent event) {
        challengeCompletionChecker.checkCompletion(event.getUserId(), event.getChallengeUlid());
    }

    @EventListener
    @Synchronized
    public void handleChallengeLeft(ChallengeLeftEvent event) {
        trophyManagementService.deleteTrophyIfExists(event.getUserId(), event.getChallengeUlid());
    }
}