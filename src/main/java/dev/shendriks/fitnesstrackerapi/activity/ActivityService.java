package dev.shendriks.fitnesstrackerapi.activity;

import dev.shendriks.fitnesstrackerapi.application.Application;
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
