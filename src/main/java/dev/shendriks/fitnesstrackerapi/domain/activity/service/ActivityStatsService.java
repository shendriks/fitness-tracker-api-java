package dev.shendriks.fitnesstrackerapi.domain.activity.service;

import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.activity.projection.ActivityAggregation;
import dev.shendriks.fitnesstrackerapi.domain.activity.projection.ActivityAggregationImpl;
import dev.shendriks.fitnesstrackerapi.domain.activity.projection.ActivityTypeAggregation;
import dev.shendriks.fitnesstrackerapi.domain.activity.repository.ActivityRepository;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import dev.shendriks.fitnesstrackerapi.supportive.TimeRange;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

@Service
public class ActivityStatsService {
    private final ActivityRepository activityRepository;

    public ActivityStatsService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public HashMap<ActivityType, ActivityAggregation> aggregateActivitiesByType(User user, TimeRange timeRange) {
        return aggregateActivitiesByTypeAsMap(user, timeRange.from(), timeRange.to());
    }

    public HashMap<ActivityType, ActivityAggregation> aggregateActivitiesByType(User user) {
        return aggregateActivitiesByTypeAsMap(user, null, null);
    }

    public ActivityAggregation aggregateActivities(User user, TimeRange timeRange) {
        return activityRepository.aggregate(user, timeRange.from(), timeRange.to());
    }

    public ActivityAggregation aggregateActivities(User user) {
        return activityRepository.aggregate(user, null, null);
    }

    private HashMap<ActivityType, ActivityAggregation> aggregateActivitiesByTypeAsMap(User user, Instant from, Instant to) {
        List<ActivityTypeAggregation> activityStatsByTypes = activityRepository.aggregateByType(user, from, to);

        HashMap<ActivityType, ActivityAggregation> activityStatsByType = new HashMap<>();
        for (ActivityTypeAggregation activityStat : activityStatsByTypes) {
            activityStatsByType.put(activityStat.getType(), new ActivityAggregationImpl(
                activityStat.getCount(),
                activityStat.getTotalDistance(),
                activityStat.getTotalDuration(),
                activityStat.getMaxDistance(),
                activityStat.getMaxDuration()
            ));
        }

        return activityStatsByType;
    }
}
