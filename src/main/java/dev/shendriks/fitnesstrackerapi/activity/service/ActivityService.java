package dev.shendriks.fitnesstrackerapi.activity.service;

import dev.shendriks.fitnesstrackerapi.activity.dto.ActivityRequest;
import dev.shendriks.fitnesstrackerapi.activity.dto.ActivityResponse;
import dev.shendriks.fitnesstrackerapi.activity.entity.Activity;
import dev.shendriks.fitnesstrackerapi.activity.mapper.ActivityMapper;
import dev.shendriks.fitnesstrackerapi.activity.repository.ActivityRepository;
import dev.shendriks.fitnesstrackerapi.application.entity.Application;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ActivityService {
    private final ActivityRepository repository;
    private final ActivityMapper mapper;

    public ActivityService(ActivityRepository repository, ActivityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ActivityResponse save(ActivityRequest request, Application application) {
        Activity activity = mapper.toEntity(request, application);
        repository.save(activity);
        return mapper.toResponse(activity);
    }

    public Iterable<ActivityResponse> getAllActivities() {
        Iterable<Activity> activities = repository.findAll(Sort.by("id").descending());

        return mapper.toResponses(activities);
    }
}
