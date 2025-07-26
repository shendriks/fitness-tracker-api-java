//package dev.shendriks.fitnesstrackerapi.domain.milestone.eventlistener;
//
//import dev.shendriks.fitnesstrackerapi.domain.achievement.entity.ActivityMetric;
//import dev.shendriks.fitnesstrackerapi.domain.activity.entity.Activity;
//import dev.shendriks.fitnesstrackerapi.domain.activity.event.ActivitySavedEvent;
//import dev.shendriks.fitnesstrackerapi.domain.activity.repository.ActivityRepository;
//import dev.shendriks.fitnesstrackerapi.domain.challenge.repository.ChallengeParticipationRepository;
//import dev.shendriks.fitnesstrackerapi.domain.milestone.entity.Milestone;
//import dev.shendriks.fitnesstrackerapi.domain.milestone.repository.MilestoneRepository;
//import dev.shendriks.fitnesstrackerapi.domain.trophy.entity.Trophy;
//import dev.shendriks.fitnesstrackerapi.domain.trophy.event.TrophyUnlockedEvent;
//import dev.shendriks.fitnesstrackerapi.domain.trophy.repository.TrophyRepository;
//import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
//import dev.shendriks.fitnesstrackerapi.domain.user.repository.UserRepository;
//import lombok.Synchronized;
//import lombok.extern.java.Log;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.context.event.EventListener;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Log
//@Component
//public class MilestoneFulfillmentChecker {
//    private final UserRepository userRepository;
//    private final ActivityRepository activityRepository;
//    private final ChallengeParticipationRepository challengeParticipationRepository;
//    private final MilestoneRepository milestoneRepository;
//    private final TrophyRepository trophyRepository;
//    private final ApplicationEventPublisher eventPublisher;
//
//    public MilestoneFulfillmentChecker(
//        UserRepository userRepository,
//        ActivityRepository activityRepository,
//        ChallengeParticipationRepository challengeParticipationRepository,
//        MilestoneRepository milestoneRepository,
//        TrophyRepository trophyRepository,
//        ApplicationEventPublisher eventPublisher
//    ) {
//        this.userRepository = userRepository;
//        this.activityRepository = activityRepository;
//        this.challengeParticipationRepository = challengeParticipationRepository;
//        this.milestoneRepository = milestoneRepository;
//        this.trophyRepository = trophyRepository;
//        this.eventPublisher = eventPublisher;
//    }
//
//    @EventListener
//    @Synchronized
//    @Async
//    public void handleSavedActivity(ActivitySavedEvent event) {
//        return;
////        User user = userRepository.findById(event.getUserId()).orElseThrow();
////
////        List<Long> alreadyCompletedAchievementIds = user
////            .getTrophies()
////            .stream()
////            .map(trophy -> trophy.getAchievement().getId())
////            .toList();
////
////        checkMilestones(event, alreadyCompletedAchievementIds, user);
//    }
//
//    private void checkMilestones(ActivitySavedEvent event, List<Long> alreadyAchievedAchievementsIds, User user) {
//        long activityCount = activityRepository.countByUser(user);
//
//        Map<ActivityMetric, Long> ruleData = new HashMap<>();
//        ruleData.put(ActivityMetric.ACTIVITY_COUNT, activityCount);
//
//        Iterable<Milestone> milestones = milestoneRepository.findByIdNotIn(alreadyAchievedAchievementsIds);
//        Activity activity = activityRepository.findById(event.getActivityId()).orElseThrow();
//
//        for (var milestone : milestones) {
//            ActivityMetric activityMetric = milestone.getActivityMetric();
//            if (!ruleData.containsKey(activityMetric)) {
//                log.warning("Failed to check milestone %s: activity metric %s is not supported".formatted(milestone.getName(), activityMetric));
//                continue;
//            }
//
//            Long currentValueOfActivityMetric = ruleData.get(activityMetric);
//            if (currentValueOfActivityMetric >= milestone.getCompletionThreshold()) {
//                Trophy trophy = new Trophy();
//                trophy.setUser(user);
//                trophy.setAchievement(milestone);
//                trophy.setActivity(activity);
//                trophyRepository.save(trophy);
//                eventPublisher.publishEvent(new TrophyUnlockedEvent(this, trophy.getId()));
//            }
//        }
//    }
//}
