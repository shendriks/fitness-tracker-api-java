package dev.shendriks.fitnesstrackerapi.domain.achievement.eventlistener;

import dev.shendriks.fitnesstrackerapi.domain.activity.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.activity.event.ActivityDeletedEvent;
import dev.shendriks.fitnesstrackerapi.domain.activity.event.ActivitySavedEvent;
import dev.shendriks.fitnesstrackerapi.domain.activity.event.ActivityUpdatedEvent;
import dev.shendriks.fitnesstrackerapi.domain.activity.repository.ActivityRepository;
import dev.shendriks.fitnesstrackerapi.domain.challenge.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.challenge.event.ChallengeJoinedEvent;
import dev.shendriks.fitnesstrackerapi.domain.challenge.event.ChallengeLeftEvent;
import dev.shendriks.fitnesstrackerapi.domain.challenge.repository.ChallengeRepository;
import dev.shendriks.fitnesstrackerapi.domain.challenge.service.ChallengeCompletionChecker;
import dev.shendriks.fitnesstrackerapi.domain.milestone.service.MilestoneCompletionChecker;
import dev.shendriks.fitnesstrackerapi.domain.trophy.service.TrophyService;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.user.repository.UserRepository;
import lombok.Synchronized;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EventHandler {
    private final ChallengeCompletionChecker challengeCompletionChecker;
    private final MilestoneCompletionChecker milestoneCompletionChecker;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final ChallengeRepository challengeRepository;
    private final TrophyService trophyService;

    public EventHandler(
        ChallengeCompletionChecker challengeCompletionChecker,
        MilestoneCompletionChecker milestoneCompletionChecker,
        UserRepository userRepository,
        ActivityRepository activityRepository,
        ChallengeRepository challengeRepository,
        TrophyService trophyService
    ) {
        this.challengeCompletionChecker = challengeCompletionChecker;
        this.milestoneCompletionChecker = milestoneCompletionChecker;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.challengeRepository = challengeRepository;
        this.trophyService = trophyService;
    }

    @EventListener
    @Synchronized
    @Async
    public void handleChallengeJoined(ChallengeJoinedEvent event) {
        User user = userRepository.findById(event.getUserId()).orElseThrow();
        challengeCompletionChecker.checkCompletionForUser(user);
    }

    @EventListener
    @Synchronized
    @Async
    public void handleChallengeLeft(ChallengeLeftEvent event) {
        User user = userRepository.findById(event.getUserId()).orElseThrow();
        Challenge challenge = challengeRepository.findById(event.getChallengeId()).orElseThrow();
        trophyService.deleteTrophyForUserAndChallenge(user, challenge);
    }

    @EventListener
    @Synchronized
    @Async
    public void handleSavedActivity(ActivitySavedEvent event) {
        Activity activity = activityRepository.findById(event.getActivityId()).orElseThrow();
        User user = activity.getUser();
        milestoneCompletionChecker.checkCompletionForUser(user);
        challengeCompletionChecker.checkCompletionForUser(user);
    }

    @EventListener
    @Synchronized
    @Async
    public void handleUpdatedActivity(ActivityUpdatedEvent event) {
        Activity activity = activityRepository.findById(event.getActivityId()).orElseThrow();
        User user = activity.getUser();
        milestoneCompletionChecker.checkCompletionForUser(user);
        challengeCompletionChecker.checkCompletionForUser(user);
    }

    @EventListener
    @Synchronized
    @Async
    public void handleDeletedActivity(ActivityDeletedEvent event) {
        Activity activity = activityRepository.findById(event.getActivityId()).orElseThrow();
        User user = activity.getUser();
        milestoneCompletionChecker.checkCompletionForUser(user);
        challengeCompletionChecker.checkCompletionForUser(user);
    }
}