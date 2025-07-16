package dev.shendriks.fitnesstrackerapi.domain.activity.service;

import dev.shendriks.fitnesstrackerapi.domain.activity.dto.ActivityCountResponse;
import dev.shendriks.fitnesstrackerapi.domain.activity.dto.ActivityRequest;
import dev.shendriks.fitnesstrackerapi.domain.activity.dto.ActivityResponse;
import dev.shendriks.fitnesstrackerapi.domain.activity.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.activity.mapper.ActivityMapper;
import dev.shendriks.fitnesstrackerapi.domain.activity.repository.ActivityRepository;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import org.springframework.stereotype.Service;

@Service
public class ActivityService {
    private final ActivityRepository repository;
    private final ActivityMapper mapper;

    public ActivityService(ActivityRepository repository, ActivityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ActivityResponse save(ActivityRequest request, User user) {
        Activity activity = mapper.toEntity(request, user);
        repository.save(activity);
        return mapper.toResponse(activity);
    }

    public Iterable<ActivityResponse> getAllActivitiesByUser(User user) {
        Iterable<Activity> activities = repository.findAllByUserOrderByUlidDesc(user);

        return mapper.toResponses(activities);
    }

    public ActivityCountResponse getActivityCountByUser(User user) {
        long activityCount = repository.countByUser(user);
        return new ActivityCountResponse(activityCount);
    }
}
