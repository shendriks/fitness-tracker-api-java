package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.event.ActivityDeletedEvent;
import dev.shendriks.fitnesstrackerapi.application.event.ActivitySavedEvent;
import dev.shendriks.fitnesstrackerapi.application.event.ActivityUpdatedEvent;
import dev.shendriks.fitnesstrackerapi.application.exception.ActivityNotFoundException;
import dev.shendriks.fitnesstrackerapi.application.exception.InvalidGPXFileException;
import dev.shendriks.fitnesstrackerapi.application.port.in.activity.*;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingActivities;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAggregatingActivities;
import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.entity.ActivityDetails;
import dev.shendriks.fitnesstrackerapi.domain.service.gpx.GpxService;
import dev.shendriks.fitnesstrackerapi.domain.service.gpx.SpeedCalculator;
import dev.shendriks.fitnesstrackerapi.domain.service.gpx.TrackPreviewService;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.InvalidObjectException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ActivityService implements
    CountActivitiesUseCase,
    ListActivitiesUseCase,
    ShowActivityDetailsUseCase,
    CreateManualActivityUseCase,
    UpdateActivityUseCase,
    UploadActivityUseCase,
    DeleteActivityUseCase,
    ShowActivityStatsUseCase {
    public static final int TRACK_PREVIEW_IMAGE_WIDTH = 200;
    public static final int TRACK_PREVIEW_IMAGE_HEIGHT = 150;

    private final ForAccessingActivities forAccessingActivities;
    private final ForAggregatingActivities forAggregatingActivities;
    private final ApplicationEventPublisher eventPublisher;
    private final GpxService gpxService;
    private final SpeedCalculator speedCalculator;
    private final TrackPreviewService trackPreviewService;

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
    public ActivityDetails saveManualActivityForUser(UserId userId, ActivityCreationData activityCreationData) {
        Speed averageSpeed = speedCalculator.calculateSpeed(
            activityCreationData.distance(),
            activityCreationData.duration()
        );
        ActivityDetails activity = forAccessingActivities.saveManualActivityForUser(userId, activityCreationData, averageSpeed);
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
                ImageData trackPreview = trackPreviewService.createTrackPreview(
                    gpsTrackData.gpsPositions(),
                    TRACK_PREVIEW_IMAGE_WIDTH,
                    TRACK_PREVIEW_IMAGE_HEIGHT
                );
                ActivityDetails activity = forAccessingActivities.saveUploadedActivityForUser(
                    userId,
                    activityUploadData,
                    gpsTrackData,
                    trackPreview
                );
                eventPublisher.publishEvent(new ActivitySavedEvent(userId, activity.id()));
                return activity;
            } finally {
                Files.deleteIfExists(tempFile);
            }
        } catch (InvalidObjectException e) {
            log.info("Error parsing GPX file", e);
            throw new InvalidGPXFileException();
        } catch (Throwable e) {
            log.error("Failed to process uploaded gpx file", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteActivityForUser(UserId userId, ActivityUlid activityUlid) {
        forAccessingActivities.deleteForUser(userId, activityUlid);
        eventPublisher.publishEvent(new ActivityDeletedEvent(userId, activityUlid));
    }

    @Override
    public ActivityAggregationMap getActivityStatsByUser(UserId id, Instant start, Instant end) {
        return forAggregatingActivities.aggregateForUserInTimeRange(id, start, end);
    }
}
