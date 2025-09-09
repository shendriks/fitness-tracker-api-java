package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.event.ActivityDeletedEvent;
import dev.shendriks.fitnesstrackerapi.application.event.ActivitySavedEvent;
import dev.shendriks.fitnesstrackerapi.application.event.ActivityUpdatedEvent;
import dev.shendriks.fitnesstrackerapi.application.exception.ActivityNotFoundException;
import dev.shendriks.fitnesstrackerapi.application.port.in.activity.*;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingActivities;
import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.entity.ActivityDetails;
import dev.shendriks.fitnesstrackerapi.domain.service.gpx.GpxService;
import dev.shendriks.fitnesstrackerapi.domain.service.gpx.RoutePreviewService;
import dev.shendriks.fitnesstrackerapi.domain.service.gpx.SpeedCalculator;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
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
    private final SpeedCalculator speedCalculator;
    private final RoutePreviewService routePreviewService;

    @Override
    public long getActivityCountByUser(UserId userId) {
        return forAccessingActivities.countByUser(userId);
    }

    @Override
    public List<Activity> getAllActivitiesByUser(UserId userId) {
        return forAccessingActivities.findAllByUser(userId);
    }

    @Override
    public ActivityDetails getActivityByUser(UserId userId, ActivityUlid activityId) {
        return forAccessingActivities
            .findByUserAndId(userId, activityId)
            .orElseThrow(ActivityNotFoundException::new);
    }

    @Override
    public ActivityDetails saveActivityForUser(UserId userId, ActivityCreationData activityCreationData) {
        Speed averageSpeed = speedCalculator.calculateSpeed(
            activityCreationData.distance(),
            activityCreationData.duration()
        );
        ActivityDetails activity = forAccessingActivities.saveForUser(userId, activityCreationData, averageSpeed);
        eventPublisher.publishEvent(new ActivitySavedEvent(userId, activity.id()));
        return activity;
    }

    @Override
    public ActivityDetails updateActivityForUser(UserId userId, ActivityUlid activityUlid, ActivityUpdateData activityUpdateData) {
        ActivityDetails activity = forAccessingActivities.updateForUser(userId, activityUlid, activityUpdateData);
        eventPublisher.publishEvent(new ActivityUpdatedEvent(userId, activity.id()));
        return activity;
    }

    @Override
    public ActivityDetails uploadActivityForUser(UserId userId, ActivityUploadData activityUploadData) {
        try {
            Path tempFile = Files.createTempFile("activity-upload-", ".gpx");
            try {
                activityUploadData.gpxFile().transferTo(tempFile);
                GPSTrackData gpsTrackData = gpxService.processGpxFile(tempFile);
                ImageData imageData = routePreviewService.createPreview(gpsTrackData.gpsPositions(), 200, 150);
                ActivityDetails activity = forAccessingActivities.saveForUser(userId, activityUploadData, gpsTrackData, imageData);
                eventPublisher.publishEvent(new ActivitySavedEvent(userId, activity.id()));
                return activity;
            } finally {
                Files.deleteIfExists(tempFile);
            }
        } catch (IOException e) {
            log.error("Failed to process uploaded gpx file", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteActivityForUser(UserId userId, ActivityUlid activityUlid) {
        forAccessingActivities.deleteForUser(userId, activityUlid);
        eventPublisher.publishEvent(new ActivityDeletedEvent(userId, activityUlid));
    }
}
