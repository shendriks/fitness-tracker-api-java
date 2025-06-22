package dev.shendriks.fitnesstrackerapi.activity.mapper;

import dev.shendriks.fitnesstrackerapi.activity.dto.ActivityRequest;
import dev.shendriks.fitnesstrackerapi.activity.dto.ActivityResponse;
import dev.shendriks.fitnesstrackerapi.activity.entity.Activity;
import dev.shendriks.fitnesstrackerapi.application.entity.Application;
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
            activity.getId(),
            activity.getUsername(),
            activity.getActivity(),
            activity.getDuration(),
            activity.getCalories(),
            activity.getApplication().getName()
        );
    }

    public Activity toEntity(ActivityRequest request, Application application) {
        Activity activity = new Activity();
        activity.setUsername(request.username().trim());
        activity.setActivity(request.activity().trim());
        activity.setDuration(request.duration());
        activity.setCalories(request.calories());
        activity.setApplication(application);
        return activity;
    }
}
