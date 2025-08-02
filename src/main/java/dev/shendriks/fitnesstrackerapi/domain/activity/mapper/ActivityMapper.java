package dev.shendriks.fitnesstrackerapi.domain.activity.mapper;

import dev.shendriks.fitnesstrackerapi.domain.activity.dto.ActivityCreateRequest;
import dev.shendriks.fitnesstrackerapi.domain.activity.dto.ActivityResponse;
import dev.shendriks.fitnesstrackerapi.domain.activity.dto.ActivityUploadRequest;
import dev.shendriks.fitnesstrackerapi.domain.activity.dto.GPSPositionResponse;
import dev.shendriks.fitnesstrackerapi.domain.activity.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.activity.entity.GPSPosition;
import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityState;
import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.gpx.dto.GpxMetricsResponse;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            activity.getUpdatedAt(),
            activity.getTitle(),
            activity.getDescription(), 
            activity.getDistance(),
            activity.getStartDate(),
            activity
                .getGpsPositions()
                .stream()
                .map(gpsPosition -> new GPSPositionResponse(
                    gpsPosition.getTimestamp(),
                    gpsPosition.getLatitude(),
                    gpsPosition.getLongitude()
                )).collect(Collectors.toList())
        );
    }

    public Activity toEntity(ActivityCreateRequest request, User user) {
        Activity activity = new Activity();
        activity.setUser(user);
        activity.setActivityType(ActivityType.fromString(request.activityType().trim()));
        activity.setDuration(request.duration());
        activity.setCalories(request.calories());
        activity.setTitle(request.title());
        activity.setDescription(request.description());
        activity.setDistance(request.distance());
        activity.setStartDate(request.startDate());
        activity.setState(ActivityState.FINISHED);
        return activity;
    }
    
    public Activity toEntity(ActivityUploadRequest request,  GpxMetricsResponse metrics, User user) {
        Activity activity = new Activity();
        activity.setUser(user);
        activity.setActivityType(ActivityType.fromString(request.activityType()));
        activity.setTitle(request.title());
        activity.setDescription(request.description());
        activity.setStartDate(metrics.gpxTime().orElse(Instant.now()));
        activity.setDuration((int) metrics.duration());
        activity.setDistance((int) metrics.totalLength());
        activity.setCalories(0);
        activity.setGpsPositions(
            metrics
                .gpsPositions()
                .stream()
                .map(ActivityMapper::toGPSPosition)
                .peek((position) -> position.setActivity(activity))
                .toList()
        );
        return activity;
    }

    private static GPSPosition toGPSPosition(GPSPositionResponse positionResponse) {
        var entity = new GPSPosition();
        entity.setLatitude(positionResponse.latitude());
        entity.setLongitude(positionResponse.longitude());
        entity.setTimestamp(positionResponse.timestamp());
        return entity;
    }
}
