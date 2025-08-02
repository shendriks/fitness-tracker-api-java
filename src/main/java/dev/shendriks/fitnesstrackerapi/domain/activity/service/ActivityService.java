package dev.shendriks.fitnesstrackerapi.domain.activity.service;

import dev.shendriks.fitnesstrackerapi.domain.activity.dto.*;
import dev.shendriks.fitnesstrackerapi.domain.activity.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.activity.event.ActivityDeletedEvent;
import dev.shendriks.fitnesstrackerapi.domain.activity.event.ActivitySavedEvent;
import dev.shendriks.fitnesstrackerapi.domain.activity.event.ActivityUpdatedEvent;
import dev.shendriks.fitnesstrackerapi.domain.activity.exception.ActivityNotFoundException;
import dev.shendriks.fitnesstrackerapi.domain.activity.mapper.ActivityMapper;
import dev.shendriks.fitnesstrackerapi.domain.activity.repository.ActivityRepository;
import dev.shendriks.fitnesstrackerapi.domain.gpx.dto.GpxMetricsResponse;
import dev.shendriks.fitnesstrackerapi.domain.gpx.service.GpxService;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import lombok.extern.java.Log;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
@Log
public class ActivityService {
    private final ActivityRepository repository;
    private final ActivityMapper mapper;
    private final ApplicationEventPublisher eventPublisher;
    private final GpxService gpxService;

    public ActivityService(
        ActivityRepository repository,
        ActivityMapper mapper,
        ApplicationEventPublisher eventPublisher,
        GpxService gpxService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
        this.gpxService = gpxService;
    }

    public ActivityResponse save(User user, ActivityCreateRequest request) {
        Activity activity = mapper.toEntity(request, user);
        repository.save(activity);
        eventPublisher.publishEvent(new ActivitySavedEvent(this, activity.getId()));
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

    public ActivityResponse findActivityByUserAndId(User user, String id) {
        Activity activity = repository.findByUserAndUlid(user, id).orElseThrow(ActivityNotFoundException::new);
        return mapper.toResponse(activity);
    }

    public void deleteActivity(User user, String id) {
        Activity activity = repository.findByUserAndUlid(user, id).orElseThrow(ActivityNotFoundException::new);
        repository.delete(activity);
        eventPublisher.publishEvent(new ActivityDeletedEvent(this, user.getId(), activity.getTitle()));
    }

    public void update(User user, String id, ActivityUpdateRequest request) {
        Activity activity = repository.findByUserAndUlid(user, id).orElseThrow(ActivityNotFoundException::new);
        activity.setActivityType(ActivityType.fromString(request.activityType().trim()));
        activity.setTitle(request.title());
        activity.setDescription(request.description());
        repository.save(activity);
        eventPublisher.publishEvent(new ActivityUpdatedEvent(this, activity.getId()));
    }

    public ActivityResponse upload(User user, ActivityUploadRequest request) throws IOException {
        Path tempFile = Files.createTempFile("activity-upload-", ".gpx");
        try {
            Files.copy(request.file().getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
            GpxMetricsResponse metrics = gpxService.processGpxFile(tempFile);
            Activity activity = mapper.toEntity(request, metrics, user);
            repository.save(activity);
            eventPublisher.publishEvent(new ActivitySavedEvent(this, activity.getId()));
            return mapper.toResponse(activity);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
