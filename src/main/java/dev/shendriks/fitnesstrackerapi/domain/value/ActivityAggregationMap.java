package dev.shendriks.fitnesstrackerapi.domain.value;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ActivityAggregationMap {
    @Getter
    private final ActivityAggregation total;
    private final HashMap<ActivityType, ActivityAggregation> byType;

    private ActivityAggregationMap(ActivityAggregation total, HashMap<ActivityType, ActivityAggregation> byTypeMap) {
        this.total = total;
        this.byType = byTypeMap;
    }

    public static ActivityAggregationMap create(ActivityAggregation total, List<ActivityTypeAggregation> byType) {
        HashMap<ActivityType, ActivityAggregation> byTypeMap = new HashMap<>();
        for (ActivityTypeAggregation activityStat : byType) {
            byTypeMap.put(activityStat.type(), new ActivityAggregation(
                activityStat.count(),
                activityStat.totalDistance(),
                activityStat.totalDuration(),
                activityStat.maxDistance(),
                activityStat.maxDuration()
            ));
        }

        Arrays
            .stream(ActivityType.values())
            .forEach(activityType -> byTypeMap
                .computeIfAbsent(activityType, key -> ActivityAggregation.zero())
            );

        return new ActivityAggregationMap(total, byTypeMap);
    }

    public ActivityAggregation getByType(ActivityType activityType) {
        return byType.getOrDefault(activityType, ActivityAggregation.zero());
    }
}
