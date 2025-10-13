package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.event.ActivityDeletedEvent;
import dev.shendriks.fitnesstrackerapi.application.event.ActivitySavedEvent;
import dev.shendriks.fitnesstrackerapi.application.event.ActivityUpdatedEvent;
import dev.shendriks.fitnesstrackerapi.application.exception.ActivityNotFoundException;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingActivities;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAggregatingActivities;
import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.entity.ActivityDetails;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.service.gpx.GpxService;
import dev.shendriks.fitnesstrackerapi.domain.service.gpx.SpeedCalculator;
import dev.shendriks.fitnesstrackerapi.domain.service.gpx.TrackPreviewService;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActivityServiceTest {
    private final UserId userId = new UserId(42L);
    private final ActivityUlid activityUlid = new ActivityUlid("TESTULID000000000000000001");
    private final ActivityId activityId = new ActivityId(100L);
    private ForAccessingActivities forAccessingActivities;
    private ForAggregatingActivities forAggregatingActivities;
    private ApplicationEventPublisher eventPublisher;
    private GpxService gpxService;
    private SpeedCalculator speedCalculator;
    private ActivityService service;
    private TrackPreviewService trackPreviewService;

    @BeforeEach
    void setUp() {
        forAccessingActivities = mock(ForAccessingActivities.class);
        forAggregatingActivities = mock(ForAggregatingActivities.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        gpxService = mock(GpxService.class);
        speedCalculator = mock(SpeedCalculator.class);
        trackPreviewService = mock(TrackPreviewService.class);
        service = new ActivityService(
            forAccessingActivities,
            forAggregatingActivities,
            eventPublisher,
            gpxService,
            speedCalculator,
            trackPreviewService
        );
    }

    @Test
    void getActivityCountByUser_delegatesToPort() {
        when(forAccessingActivities.countByUser(userId)).thenReturn(5L);

        long actualCount = service.getActivityCountByUser(userId);

        assertEquals(5L, actualCount);
        verify(forAccessingActivities).countByUser(userId);
        verifyNoMoreInteractions(forAccessingActivities, eventPublisher, gpxService);
    }

    @Test
    void getAllActivitiesByUser_returnsList() {
        Activity activity = Activity.builder()
            .id(activityId)
            .ulid(activityUlid)
            .activityType(ActivityType.RUNNING)
            .duration(Duration.ofSeconds(3600L))
            .distance(Distance.ofMeters(10000.0))
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .title("Morning Run")
            .description("Nice run")
            .startDate(Instant.now())
            .build();
        when(forAccessingActivities.findAllByUser(userId)).thenReturn(List.of(activity));

        List<Activity> actualActivities = service.getAllActivitiesByUser(userId);

        assertEquals(1, actualActivities.size());
        assertEquals(activity, actualActivities.getFirst());
        verify(forAccessingActivities).findAllByUser(userId);
        verifyNoMoreInteractions(forAccessingActivities, eventPublisher, gpxService);
    }

    @Test
    void getActivityByUser_withFound_returnsActivity() {
        ActivityDetails activity = ActivityDetails
            .builder()
            .id(activityId)
            .ulid(activityUlid)
            .activityType(ActivityType.WALKING)
            .duration(Duration.ofSeconds(10L))
            .distance(Distance.ofMeters(0.0))
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .title("")
            .description("")
            .startDate(Instant.now())
            .gpsPositions(List.of())
            .build();
        when(forAccessingActivities.findByUserAndId(userId, activityUlid)).thenReturn(Optional.of(activity));

        ActivityDetails actualActivity = service.getActivityByUser(userId, activityUlid);

        assertEquals(activity, actualActivity);
        verify(forAccessingActivities).findByUserAndId(userId, activityUlid);
        verifyNoMoreInteractions(forAccessingActivities, eventPublisher, gpxService);
    }

    @Test
    void getActivityByUser_withNotFound_throwsActivityNotFoundException() {
        when(forAccessingActivities.findByUserAndId(userId, activityUlid)).thenReturn(Optional.empty());

        assertThrows(ActivityNotFoundException.class, () -> service.getActivityByUser(userId, activityUlid));
        verify(forAccessingActivities).findByUserAndId(userId, activityUlid);
        verifyNoMoreInteractions(forAccessingActivities, eventPublisher, gpxService);
    }

    @Test
    void saveActivityForUser_publishesManualActivitySavedEvent() {
        ActivityCreationData creationData = ActivityCreationData
            .builder()
            .activityType(ActivityType.CYCLING)
            .duration(Duration.ofSeconds(1800L))
            .distance(Distance.ofMeters(15000.0))
            .title("Ride")
            .description("Desc")
            .startDate(Instant.now())
            .build();
        Speed averageSpeed = Speed.ofMetersPerSecond(1800 / 15000.0);
        ActivityDetails activity = ActivityDetails
            .builder()
            .id(activityId)
            .ulid(activityUlid)
            .activityType(ActivityType.CYCLING)
            .duration(Duration.ofSeconds(1800L))
            .distance(Distance.ofMeters(15000.0))
            .averageSpeed(averageSpeed)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .title("Ride")
            .description("Desc")
            .startDate(Instant.now())
            .gpsPositions(List.of())
            .build();
        when(forAccessingActivities.saveManualActivityForUser(userId, creationData, averageSpeed)).thenReturn(activity);
        when(speedCalculator.calculateSpeed(Distance.ofMeters(15000.0), Duration.ofSeconds(1800L)))
            .thenReturn(averageSpeed);

        ActivityDetails actualActivity = service.saveManualActivityForUser(userId, creationData);

        assertEquals(activity, actualActivity);
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertInstanceOf(ActivitySavedEvent.class, event);
        ActivitySavedEvent activitySavedEvent = (ActivitySavedEvent) event;
        assertEquals(userId, activitySavedEvent.userId());
        assertEquals(activityId, activitySavedEvent.activityId());
        verify(forAccessingActivities).saveManualActivityForUser(userId, creationData, averageSpeed);
        verifyNoMoreInteractions(forAccessingActivities, gpxService);
    }

    @Test
    void updateActivityForUser_publishesActivityUpdatedEvent() {
        ActivityUpdateData updateData = new ActivityUpdateData(ActivityType.SWIMMING, "Swim", "Pool");
        ActivityDetails updated = ActivityDetails
            .builder()
            .id(activityId)
            .ulid(activityUlid)
            .activityType(ActivityType.SWIMMING)
            .duration(Duration.ofSeconds(1200L))
            .distance(Distance.ofMeters(1000.0))
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .title("Swim")
            .description("Pool")
            .startDate(Instant.now())
            .gpsPositions(List.of())
            .build();
        when(forAccessingActivities.updateForUser(userId, activityUlid, updateData)).thenReturn(updated);

        ActivityDetails result = service.updateActivityForUser(userId, activityUlid, updateData);

        assertEquals(updated, result);
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertInstanceOf(ActivityUpdatedEvent.class, event);
        ActivityUpdatedEvent activityUpdatedEvent = (ActivityUpdatedEvent) event;
        assertEquals(userId, activityUpdatedEvent.userId());
        assertEquals(activityId, activityUpdatedEvent.activityId());
        verify(forAccessingActivities).updateForUser(userId, activityUlid, updateData);
        verifyNoMoreInteractions(forAccessingActivities, gpxService);
    }

    @Test
    void uploadActivityForUser_processesAndPublishesEvent() throws IOException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        ActivityUploadData uploadData = ActivityUploadData
            .builder()
            .activityType(ActivityType.RUNNING)
            .title("Run from GPX")
            .description("desc")
            .gpxFile(multipartFile)
            .build();

        GPSTrackData gpsTrackData = GPSTrackData
            .builder()
            .name("track")
            .gpxTime(Optional.empty())
            .distance(Distance.ofMeters(1000.0))
            .duration(Duration.ofSeconds(600L))
            .speed(Speed.ofMetersPerSecond(10.0))
            .elevationGain(Distance.ofMeters(10.0))
            .motionTime(Duration.ofSeconds(500L))
            .pausingTime(Duration.ofSeconds(100L))
            .kilometerSpeeds(List.of())
            .gpsPositions(List.of())
            .build();
        when(gpxService.processGpxFile(any())).thenReturn(gpsTrackData);

        ActivityDetails activity = ActivityDetails
            .builder()
            .id(activityId)
            .ulid(activityUlid)
            .activityType(ActivityType.RUNNING)
            .duration(Duration.ofSeconds(600L))
            .distance(Distance.ofMeters(1000.0))
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .title("Run from GPX")
            .description("desc")
            .startDate(Instant.now())
            .gpsPositions(List.of())
            .build();

        ImageData imageData = new ImageData("some-dummy-data".getBytes());

        when(trackPreviewService.createTrackPreview(gpsTrackData.gpsPositions(), 200, 150)).thenReturn(imageData);
        when(forAccessingActivities.saveUploadedActivityForUser(userId, uploadData, gpsTrackData, imageData)).thenReturn(activity);

        ActivityDetails actualActivity = service.uploadActivityForUser(userId, uploadData);

        assertEquals(activity, actualActivity);
        verify(multipartFile, times(1)).transferTo(any(Path.class));
        verify(gpxService, times(1)).processGpxFile(any());
        verify(trackPreviewService, times(1)).createTrackPreview(gpsTrackData.gpsPositions(), 200, 150);
        verify(forAccessingActivities, times(1)).saveUploadedActivityForUser(userId, uploadData, gpsTrackData, imageData);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertInstanceOf(ActivitySavedEvent.class, eventCaptor.getValue());
        ActivitySavedEvent activitySavedEvent = (ActivitySavedEvent) eventCaptor.getValue();
        assertEquals(userId, activitySavedEvent.userId());
        assertEquals(activityId, activitySavedEvent.activityId());
        verifyNoMoreInteractions(gpxService);
    }

    @Test
    void uploadActivityForUser_wrapsIOException() throws IOException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        doThrow(new IOException("boom")).when(multipartFile).transferTo(any(Path.class));
        ActivityUploadData uploadData = ActivityUploadData
            .builder()
            .activityType(ActivityType.RUNNING)
            .title("Run from GPX")
            .description("desc")
            .gpxFile(multipartFile)
            .build();

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> service.uploadActivityForUser(userId, uploadData)
        );
        assertInstanceOf(IOException.class, exception.getCause());
        verify(multipartFile).transferTo(any(Path.class));
        verifyNoInteractions(gpxService);
        verifyNoMoreInteractions(forAccessingActivities, eventPublisher);
    }

    @Test
    void deleteActivityForUser_publishesActivityDeletedEvent() {
        service.deleteActivityForUser(userId, activityUlid);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(forAccessingActivities).deleteForUser(userId, activityUlid);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertInstanceOf(ActivityDeletedEvent.class, event);
        ActivityDeletedEvent activityDeletedEvent = (ActivityDeletedEvent) event;
        assertEquals(userId, activityDeletedEvent.userId());
        assertEquals(activityUlid, activityDeletedEvent.activityTitle());
        verifyNoMoreInteractions(forAccessingActivities, gpxService);
    }

    @Test
    void getActivityStatsByUser_delegatesToPortAndReturnsMap() {
        Instant start = Instant.parse("2024-01-01T00:00:00Z");
        Instant end = Instant.parse("2025-01-01T00:00:00Z");

        ActivityAggregation total = ActivityAggregation
            .builder()
            .count(3L)
            .totalDistance(Distance.ofMeters(30000.0))
            .totalDuration(Duration.ofSeconds(7200L))
            .maxDistance(Distance.ofMeters(15000.0))
            .maxDuration(Duration.ofSeconds(3600L))
            .build();

        ActivityTypeAggregation runningAgg = ActivityTypeAggregation
            .builder()
            .type(ActivityType.RUNNING)
            .count(2L)
            .totalDistance(Distance.ofMeters(20000.0))
            .totalDuration(Duration.ofSeconds(4800L))
            .maxDistance(Distance.ofMeters(12000.0))
            .maxDuration(Duration.ofSeconds(3000L))
            .build();

        ActivityTypeAggregation cyclingAgg = ActivityTypeAggregation
            .builder()
            .type(ActivityType.CYCLING)
            .count(1L)
            .totalDistance(Distance.ofMeters(10000.0))
            .totalDuration(Duration.ofSeconds(2400L))
            .maxDistance(Distance.ofMeters(10000.0))
            .maxDuration(Duration.ofSeconds(2400L))
            .build();

        ActivityAggregationMap expected = ActivityAggregationMap.create(total, List.of(runningAgg, cyclingAgg));
        when(forAggregatingActivities.aggregateForUserInTimeRange(userId, start, end)).thenReturn(expected);

        ActivityAggregationMap actual = service.getActivityStatsByUser(userId, start, end);

        assertEquals(expected.getTotal(), actual.getTotal());
        assertEquals(expected.getByType(ActivityType.RUNNING).totalDistance(), actual.getByType(ActivityType.RUNNING).totalDistance());
        assertEquals(expected.getByType(ActivityType.CYCLING).count(), actual.getByType(ActivityType.CYCLING).count());

        verify(forAggregatingActivities).aggregateForUserInTimeRange(userId, start, end);
        verifyNoMoreInteractions(forAggregatingActivities);
        verifyNoMoreInteractions(forAccessingActivities, gpxService, eventPublisher);
    }

    @Test
    void getActivityStatsByUser_withNoData_returnsEmptyMap() {
        Instant start = Instant.parse("2025-01-01T00:00:00Z");
        Instant end = Instant.parse("2025-02-01T00:00:00Z");

        ActivityAggregationMap empty = ActivityAggregationMap.create(ActivityAggregation.zero(), List.of());
        when(forAggregatingActivities.aggregateForUserInTimeRange(userId, start, end)).thenReturn(empty);

        ActivityAggregationMap actual = service.getActivityStatsByUser(userId, start, end);

        assertEquals(0L, actual.getTotal().count());
        assertEquals(Distance.zero(), actual.getTotal().totalDistance());
        for (ActivityType type : ActivityType.values()) {
            assertEquals(0L, actual.getByType(type).count());
        }

        verify(forAggregatingActivities).aggregateForUserInTimeRange(userId, start, end);
        verifyNoMoreInteractions(forAggregatingActivities);
        verifyNoMoreInteractions(forAccessingActivities, gpxService, eventPublisher);
    }
}
