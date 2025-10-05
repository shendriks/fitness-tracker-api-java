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

/**
 * Application service orchestrating activity-related use cases.
 *
 * <p>Handles listing, reading details, creating/updating, uploading from GPX,
 * and deleting activities while emitting domain events.</p>
 */
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

    /**
     * Returns the number of activities belonging to the given user.
     * @param userId the owner of the activities
     */
    @Override
    public long getActivityCountByUser(UserId userId) {
        return forAccessingActivities.countByUser(userId);
    }

    /**
     * Lists all activities for the given user ordered by repository defaults.
     * @param userId the owner of the activities
     * @return list of activities without full GPS details
     */
    @Override
    public List<Activity> getAllActivitiesByUser(UserId userId) {
        return forAccessingActivities.findAllByUser(userId);
    }

    /**
     * Returns the full details for a user's activity or throws if not found.
     * @param userId the owner of the activity
     * @param activityId the activity ULID
     * @return activity with GPS details
     * @throws ActivityNotFoundException if the activity does not exist for the user
     */
    @Override
    public ActivityDetails getActivityByUser(UserId userId, ActivityUlid activityId) {
        return forAccessingActivities
            .findByUserAndId(userId, activityId)
            .orElseThrow(ActivityNotFoundException::new);
    }

    /**
     * Persists a new activity for the user based on provided summary data.
     * Calculates average speed and emits an ActivitySavedEvent.
     * @param userId owner of the new activity
     * @param activityCreationData summary metrics and metadata
     * @return the persisted activity with details
     */
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

    /**
     * Updates an existing activity for the user and emits an ActivityUpdatedEvent.
     * @param userId owner of the activity
     * @param activityUlid identifier of the activity to update
     * @param activityUpdateData fields to update
     * @return the updated activity
     */
    @Override
    public ActivityDetails updateActivityForUser(UserId userId, ActivityUlid activityUlid, ActivityUpdateData activityUpdateData) {
        ActivityDetails activity = forAccessingActivities.updateForUser(userId, activityUlid, activityUpdateData);
        eventPublisher.publishEvent(new ActivityUpdatedEvent(userId, activity.id()));
        return activity;
    }

    /**
     * Processes an uploaded GPX file, generates preview image, and saves a new activity.
     * Always cleans up temporary files and emits an ActivitySavedEvent.
     * @param userId owner of the new activity
     * @param activityUploadData upload payload containing file and metadata
     * @return the persisted activity with derived metrics and preview image
     * @throws RuntimeException when IO errors occur while handling the upload
     */
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

    /**
     * Deletes a user's activity and emits an ActivityDeletedEvent.
     * @param userId owner of the activity
     * @param activityUlid identifier of the activity to delete
     */
    @Override
    public void deleteActivityForUser(UserId userId, ActivityUlid activityUlid) {
        forAccessingActivities.deleteForUser(userId, activityUlid);
        eventPublisher.publishEvent(new ActivityDeletedEvent(userId, activityUlid));
    }
}
