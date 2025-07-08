package dev.shendriks.fitnesstrackerapi.domain.activity.mapper;

import dev.shendriks.fitnesstrackerapi.domain.activity.dto.ActivityRequest;
import dev.shendriks.fitnesstrackerapi.domain.activity.dto.ActivityResponse;
import dev.shendriks.fitnesstrackerapi.domain.activity.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityState;
import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ActivityMapper {
    public List<ActivityResponse> toResponses(Iterable<Activity> activities) {
        List<ActivityResponse> activityResponses = new ArrayList<>();
        for (Activity activity : activities) {
            activityResponses.add(toResponse(activity));
        }

        return activityResponses;
    }

    public ActivityResponse toResponse(Activity activity) {
        return new ActivityResponse(
            activity.getUlid(),
            activity.getActivityType(),
            activity.getDuration(),
            activity.getCalories(),
            activity.getCreatedAt(),
            activity.getUpdatedAt()
        );
    }

    public Activity toEntity(ActivityRequest request, User user) {
        Activity activity = new Activity();
        activity.setUser(user);
        activity.setActivityType(ActivityType.fromString(request.activityType().trim()));
        activity.setDuration(request.duration());
        activity.setCalories(request.calories());
        activity.setState(ActivityState.FINISHED);
        return activity;
    }
}
