package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.event.ActivityDeletedEvent;
import dev.shendriks.fitnesstrackerapi.application.event.ActivitySavedEvent;
import dev.shendriks.fitnesstrackerapi.application.event.ActivityUpdatedEvent;
import dev.shendriks.fitnesstrackerapi.application.exception.ActivityNotFoundException;
import dev.shendriks.fitnesstrackerapi.application.port.in.activity.*;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingActivities;
import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@AllArgsConstructor
@Log
public class ActivityService implements
    CountActivitiesUseCase,
    ListActivitiesUseCase,
    ShowActivityDetailsUseCase,
    CreateActivityUseCase,
    UpdateActivityUseCase,
    UploadActivityUseCase,
    DeleteActivityUseCase {
    private final ForAccessingActivities forAccessingActivities;
    private final ApplicationEventPublisher eventPublisher;
    private final GpxService gpxService;

    @Override
    public long getActivityCountByUser(UserId userId) {
        return forAccessingActivities.countByUser(userId);
    }

    @Override
    public List<Activity> getAllActivitiesByUser(UserId userId) {
        return forAccessingActivities.findAllByUser(userId);
    }

    @Override
    public Activity getActivityByUser(UserId userId, ActivityUlid activityId) {
        return forAccessingActivities
            .findByUserAndId(userId, activityId)
            .orElseThrow(ActivityNotFoundException::new);
    }

    @Override
    public Activity saveActivityForUser(UserId userId, ActivityCreationData activityCreationData) {
        Activity activity = forAccessingActivities.saveForUser(userId, activityCreationData);
        eventPublisher.publishEvent(new ActivitySavedEvent(this, userId, activity.id()));
        return activity;
    }

    @Override
    public Activity updateActivityForUser(UserId userId, ActivityUlid activityUlid, ActivityUpdateData activityUpdateData) {
        Activity activity = forAccessingActivities.updateForUser(userId, activityUlid, activityUpdateData);
        eventPublisher.publishEvent(new ActivityUpdatedEvent(this, userId, activity.id()));
        return activity;
    }

    @Override
    public Activity uploadActivityForUser(UserId userId, ActivityUploadData activityUploadData) {
        try {
            Path tempFile = Files.createTempFile("activity-upload-", ".gpx");
            try {
                activityUploadData.gpxFile().transferTo(tempFile);
                GPSTrackData gpsTrackData = gpxService.processGpxFile(tempFile);
                Activity activity = forAccessingActivities.saveForUser(userId, activityUploadData, gpsTrackData);
                eventPublisher.publishEvent(new ActivitySavedEvent(this, userId, activity.id()));
                return activity;
            } finally {
                Files.deleteIfExists(tempFile);
            }
        } catch (IOException e) {
            log.severe("Failed to process uploaded gpxFile: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteActivityForUser(UserId userId, ActivityUlid activityUlid) {
        forAccessingActivities.deleteForUser(userId, activityUlid);
        eventPublisher.publishEvent(new ActivityDeletedEvent(this, userId, activityUlid));
    }
}
