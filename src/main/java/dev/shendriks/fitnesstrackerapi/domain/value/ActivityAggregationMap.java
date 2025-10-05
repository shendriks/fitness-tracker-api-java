package dev.shendriks.fitnesstrackerapi.domain.value;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Map-like aggregate providing total and per-activity-type aggregations.
 *
 * <p>Ensures every ActivityType is present in the map (defaulting to zero values).</p>
 */
@SuppressWarnings("ClassCanBeRecord")
public class ActivityAggregationMap {
    @Getter
    private final ActivityAggregation total;
    private final HashMap<ActivityType, ActivityAggregation> byType;

    private ActivityAggregationMap(ActivityAggregation total, HashMap<ActivityType, ActivityAggregation> byTypeMap) {
        this.total = total;
        this.byType = byTypeMap;
    }

    /**
     * Creates a map from provided totals and type-wise aggregations, filling in missing types with zero.
     * @param total overall aggregation across all activities
     * @param byType list of per-type aggregations
     * @return ActivityAggregationMap instance
     */
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

    /**
     * Returns the aggregation for the given activity type (or zero if absent).
     */
    public ActivityAggregation getByType(ActivityType activityType) {
        return byType.getOrDefault(activityType, ActivityAggregation.zero());
    }
}
