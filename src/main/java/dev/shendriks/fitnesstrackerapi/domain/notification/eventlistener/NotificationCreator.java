package dev.shendriks.fitnesstrackerapi.domain.notification.eventlistener;

import dev.shendriks.fitnesstrackerapi.domain.activity.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.activity.event.ActivityDeletedEvent;
import dev.shendriks.fitnesstrackerapi.domain.activity.event.ActivitySavedEvent;
import dev.shendriks.fitnesstrackerapi.domain.activity.event.ActivityUpdatedEvent;
import dev.shendriks.fitnesstrackerapi.domain.activity.repository.ActivityRepository;
import dev.shendriks.fitnesstrackerapi.domain.challenge.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.challenge.event.ChallengeCompletedEvent;
import dev.shendriks.fitnesstrackerapi.domain.challenge.event.ChallengeJoinedEvent;
import dev.shendriks.fitnesstrackerapi.domain.challenge.event.ChallengeLeftEvent;
import dev.shendriks.fitnesstrackerapi.domain.challenge.repository.ChallengeRepository;
import dev.shendriks.fitnesstrackerapi.domain.milestone.entity.Milestone;
import dev.shendriks.fitnesstrackerapi.domain.milestone.event.MilestoneCompletedEvent;
import dev.shendriks.fitnesstrackerapi.domain.milestone.repository.MilestoneRepository;
import dev.shendriks.fitnesstrackerapi.domain.notification.entity.Notification;
import dev.shendriks.fitnesstrackerapi.domain.notification.repository.NotificationRepository;
import dev.shendriks.fitnesstrackerapi.domain.trophy.event.TrophyLostEvent;
import dev.shendriks.fitnesstrackerapi.domain.trophy.event.TrophyUnlockedEvent;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.user.repository.UserRepository;
import lombok.Synchronized;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class NotificationCreator {
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final ChallengeRepository challengeRepository;
    private final NotificationRepository notificationRepository;
    private final MilestoneRepository milestoneRepository;

    public NotificationCreator(
        UserRepository userRepository,
        ActivityRepository activityRepository,
        ChallengeRepository challengeRepository,
        NotificationRepository notificationRepository,
        MilestoneRepository milestoneRepository) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.challengeRepository = challengeRepository;
        this.notificationRepository = notificationRepository;
        this.milestoneRepository = milestoneRepository;
    }

    @EventListener
    @Synchronized
    @Async
    public void handle(ChallengeJoinedEvent event) {
        User user = userRepository.findById(event.getUserId()).orElseThrow();
        Challenge challenge = challengeRepository.findById(event.getChallengeId()).orElseThrow();
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("Challenge joined");
        notification.setDescription("You have joined the challenge \""+ challenge.getName() + "\"");
        notificationRepository.save(notification);
    }

    @EventListener
    @Synchronized
    @Async
    public void handle(ChallengeLeftEvent event) {
        User user = userRepository.findById(event.getUserId()).orElseThrow();
        Challenge challenge = challengeRepository.findById(event.getChallengeId()).orElseThrow();
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("Challenge left");
        notification.setDescription("You have left the challenge \""+ challenge.getName() + "\"");
        notificationRepository.save(notification);
    }

    @EventListener
    @Synchronized
    @Async
    public void handle(ChallengeCompletedEvent event) {
        User user = userRepository.findById(event.getUserId()).orElseThrow();
        Challenge challenge = challengeRepository.findById(event.getChallengeId()).orElseThrow();
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("Wow, congratulations!");
        notification.setDescription("You have completed the challenge " + challenge.getName());
        notificationRepository.save(notification);
    }

    @EventListener
    @Synchronized
    @Async
    public void handle(MilestoneCompletedEvent event) {
        User user = userRepository.findById(event.getUserId()).orElseThrow();
        Milestone milestone = milestoneRepository.findById(event.getMilestoneId()).orElseThrow();
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("Another milestone reached!");
        notification.setDescription("You have completed the milestone " + milestone.getName());
        notificationRepository.save(notification);
    }

    @EventListener
    @Synchronized
    @Async
    public void handle(ActivitySavedEvent event) {
        Activity activity = activityRepository.findById(event.getActivityId()).orElseThrow();
        User user = activity.getUser();
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("Well done!");
        notification.setDescription("You have saved a new activity: " + activity.getTitle());
        notificationRepository.save(notification);
    }

    @EventListener
    @Synchronized
    @Async
    public void handle(ActivityUpdatedEvent event) {
        Activity activity = activityRepository.findById(event.getActivityId()).orElseThrow();
        User user = activity.getUser();
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("Activity updated!");
        notification.setDescription("You have updated an activity: " + activity.getTitle());
        notificationRepository.save(notification);
    }

    @EventListener
    @Synchronized
    @Async
    public void handle(ActivityDeletedEvent event) {
        User user = userRepository.findById(event.getUserId()).orElseThrow();
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("Bummer!");
        notification.setDescription("You have deleted an activity");
        notificationRepository.save(notification);
    }

    @EventListener
    @Synchronized
    @Async
    public void handle(TrophyUnlockedEvent event) {
        User user = userRepository.findById(event.getUserId()).orElseThrow();
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("Superb!");
        notification.setDescription("You have unlocked a trophy");
        notificationRepository.save(notification);
    }

    @EventListener
    @Synchronized
    @Async
    public void handle(TrophyLostEvent event) {
        User user = userRepository.findById(event.getUserId()).orElseThrow();
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("D'oh!");
        notification.setDescription("You have lost a trophy");
        notificationRepository.save(notification);
    }
}