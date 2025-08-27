package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ActivityDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.GPSPositionDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityState;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ActivityDbEntityMapperTest {
    private final ActivityDbEntityMapper mapper = Mappers.getMapper(ActivityDbEntityMapper.class);

    @BeforeEach
    void setUp() {
        mapper.setClock(Clock.fixed(Instant.parse("2021-09-10T12:00:00Z"), ZoneOffset.UTC));
    }

    @Test
    void toActivity_mapsAllFieldsAndWrapsIdsAndMapsGps() {
        ActivityDbEntity activityDbEntity = ActivityDbEntity
            .builder()
            .id(101L)
            .ulid("TESTULID000000000000000001")
            .activityType(ActivityType.CYCLING)
            .duration(3600)
            .calories(800)
            .createdAt(Instant.parse("2025-08-01T10:15:30Z"))
            .updatedAt(Instant.parse("2025-08-02T10:15:30Z"))
            .title("Morning Ride")
            .description("Sunny morning ride")
            .distance(25750)
            .startDate(Instant.parse("2025-08-01T07:00:00Z"))
            .build();
        activityDbEntity.setGpsPositions(List.of(
            GPSPositionDbEntity
                .builder()
                .activity(activityDbEntity)
                .timestamp(Instant.parse("2025-08-01T07:00:00Z"))
                .latitude(0.0)
                .longitude(1.0)
                .build(),
            GPSPositionDbEntity
                .builder()
                .activity(activityDbEntity)
                .timestamp(Instant.parse("2025-08-01T07:05:00Z"))
                .latitude(0.1)
                .longitude(1.1)
                .build()
        ));

        Activity actualActivity = mapper.toActivity(activityDbEntity);

        assertNotNull(actualActivity);
        assertEquals(new ActivityId(101L), actualActivity.id());
        assertEquals(new ActivityUlid("TESTULID000000000000000001"), actualActivity.ulid());
        assertEquals(ActivityType.CYCLING, actualActivity.activityType());
        assertEquals(3600, actualActivity.duration());
        assertEquals(800, actualActivity.calories());
        assertEquals(Instant.parse("2025-08-01T10:15:30Z"), actualActivity.createdAt());
        assertEquals(Instant.parse("2025-08-02T10:15:30Z"), actualActivity.updatedAt());
        assertEquals("Morning Ride", actualActivity.title());
        assertEquals("Sunny morning ride", actualActivity.description());
        assertEquals(25750, actualActivity.distance());
        assertEquals(Instant.parse("2025-08-01T07:00:00Z"), actualActivity.startDate());
        assertNotNull(actualActivity.gpsPositions());
        assertEquals(2, actualActivity.gpsPositions().size());
        assertEquals(0.0, actualActivity.gpsPositions().get(0).latitude());
        assertEquals(1.0, actualActivity.gpsPositions().get(0).longitude());
        assertEquals(0.1, actualActivity.gpsPositions().get(1).latitude());
        assertEquals(1.1, actualActivity.gpsPositions().get(1).longitude());
    }

    @Test
    void toActivities_mapsList() {
        List<ActivityDbEntity> activityDbEntities = List.of(
            ActivityDbEntity
                .builder()
                .id(1L)
                .ulid("TESTULID000000000000000002")
                .activityType(ActivityType.WALKING)
                .duration(100)
                .calories(10)
                .title("Walk")
                .description("desc")
                .distance(123)
                .startDate(Instant.parse("2025-08-01T00:00:00Z"))
                .build(),
            ActivityDbEntity
                .builder()
                .id(2L)
                .ulid("TESTULID000000000000000003")
                .activityType(ActivityType.SWIMMING)
                .duration(200)
                .calories(20)
                .title("Swim")
                .description("desc")
                .distance(456)
                .startDate(Instant.parse("2025-08-02T00:00:00Z"))
                .build()
        );

        List<Activity> activities = mapper.toActivities(activityDbEntities);

        assertEquals(2, activities.size());

        assertEquals(new ActivityId(1L), activities.getFirst().id());
        assertEquals(new ActivityUlid("TESTULID000000000000000002"), activities.getFirst().ulid());
        assertEquals("Walk", activities.getFirst().title());
        assertEquals("desc", activities.getFirst().description());
        assertEquals(Instant.parse("2025-08-01T00:00:00Z"), activities.getFirst().startDate());
        assertEquals(100, activities.getFirst().duration());
        assertEquals(10, activities.getFirst().calories());
        assertEquals(123, activities.getFirst().distance());
        assertEquals(ActivityType.WALKING, activities.getFirst().activityType());

        assertEquals(new ActivityId(2L), activities.get(1).id());
        assertEquals(new ActivityUlid("TESTULID000000000000000003"), activities.get(1).ulid());
        assertEquals("Swim", activities.get(1).title());
        assertEquals("desc", activities.get(1).description());
        assertEquals(Instant.parse("2025-08-02T00:00:00Z"), activities.get(1).startDate());
        assertEquals(200, activities.get(1).duration());
        assertEquals(20, activities.get(1).calories());
        assertEquals(456, activities.get(1).distance());
        assertEquals(ActivityType.SWIMMING, activities.get(1).activityType());
    }

    @Test
    void toActivityDbEntity_mapsFieldsAndIgnoreSpecifiedOnes() {
        ActivityCreationData activityCreationData = ActivityCreationData
            .builder()
            .activityType(ActivityType.RUNNING)
            .duration(1234)
            .calories(567)
            .title("Test Title")
            .description("Test Description")
            .distance(890)
            .startDate(Instant.parse("2025-08-01T12:00:00Z"))
            .build();

        ActivityDbEntity actualActivityDbEntity = mapper.toActivityDbEntity(activityCreationData);

        assertNotNull(actualActivityDbEntity);
        // mapped fields
        assertEquals(ActivityType.RUNNING, actualActivityDbEntity.getActivityType());
        assertEquals(1234, actualActivityDbEntity.getDuration());
        assertEquals(567, actualActivityDbEntity.getCalories());
        assertEquals("Test Title", actualActivityDbEntity.getTitle());
        assertEquals("Test Description", actualActivityDbEntity.getDescription());
        assertEquals(890, actualActivityDbEntity.getDistance());
        assertEquals(Instant.parse("2025-08-01T12:00:00Z"), actualActivityDbEntity.getStartDate());
        // ignored fields
        assertEquals(0L, actualActivityDbEntity.getId());
        assertNull(actualActivityDbEntity.getUlid());
        assertNull(actualActivityDbEntity.getUser());
        assertNull(actualActivityDbEntity.getCreatedAt());
        assertNull(actualActivityDbEntity.getUpdatedAt());
        assertEquals(List.of(), actualActivityDbEntity.getGpsPositions());
        assertTrue(actualActivityDbEntity.getGpsPositions().isEmpty());
        assertEquals(ActivityState.STARTED, actualActivityDbEntity.getState());
    }

    @Test
    void toActivityDbEntity_whenGpxTimePresent_mapsFromTrack() {
        ActivityUploadData activityUploadData = ActivityUploadData
            .builder()
            .activityType(ActivityType.MOUNTAIN_BIKING)
            .title("Trail Fun")
            .description("Great ride")
            .build();
        GPSTrackData gpsTrackData = GPSTrackData
            .builder()
            .name("name")
            .gpxTime(Optional.of(Instant.parse("2025-08-01T09:30:00Z")))
            .totalLength(12345.67)
            .duration(5432)
            .speed(0.0)
            .pace(0.0)
            .elevationGain(0.0)
            .motionTime(0)
            .pausingTime(0)
            .kilometerSpeeds(List.of())
            .kilometerPaces(List.of())
            .gpsPositions(List.of(
                new GPSPositionData(Instant.parse("2025-08-01T09:30:00Z"), 0.0, 1.0, Optional.empty()),
                new GPSPositionData(Instant.parse("2025-08-01T09:45:00Z"), 0.1, 1.1, Optional.of(10.0))
            ))
            .build();

        ActivityDbEntity actualActivityDbEntity = mapper.toActivityDbEntity(activityUploadData, gpsTrackData);

        assertNotNull(actualActivityDbEntity);
        assertEquals(ActivityType.MOUNTAIN_BIKING, actualActivityDbEntity.getActivityType());
        assertEquals("Trail Fun", actualActivityDbEntity.getTitle());
        assertEquals("Great ride", actualActivityDbEntity.getDescription());
        assertEquals(Instant.parse("2025-08-01T09:30:00Z"), actualActivityDbEntity.getStartDate());
        assertEquals(5432, actualActivityDbEntity.getDuration());
        assertEquals(12345, actualActivityDbEntity.getDistance());
        assertEquals(0, actualActivityDbEntity.getCalories());
        assertNotNull(actualActivityDbEntity.getGpsPositions());
        assertEquals(2, actualActivityDbEntity.getGpsPositions().size());
        for (GPSPositionDbEntity gpsPosition : actualActivityDbEntity.getGpsPositions()) {
            assertSame(actualActivityDbEntity, gpsPosition.getActivity());
        }

        assertEquals(0.0, actualActivityDbEntity.getGpsPositions().getFirst().getLatitude());
        assertEquals(1.0, actualActivityDbEntity.getGpsPositions().getFirst().getLongitude());
        assertNull(actualActivityDbEntity.getGpsPositions().getFirst().getAltitude());
        assertNull(actualActivityDbEntity.getGpsPositions().getFirst().getAccuracy());

        assertEquals(0.1, actualActivityDbEntity.getGpsPositions().get(1).getLatitude());
        assertEquals(1.1, actualActivityDbEntity.getGpsPositions().get(1).getLongitude());
        assertEquals(10.0, actualActivityDbEntity.getGpsPositions().get(1).getAltitude());
        assertNull(actualActivityDbEntity.getGpsPositions().get(1).getAccuracy());
    }

    @Test
    void toActivityDbEntity_whenNoGpxTime_usesNow() {
        ActivityUploadData request = ActivityUploadData
            .builder()
            .activityType(ActivityType.RUNNING)
            .title("Run")
            .description("desc")
            .build();
        GPSTrackData metrics = GPSTrackData
            .builder()
            .name("name")
            .gpxTime(Optional.empty())
            .totalLength(1000.0)
            .duration(100)
            .speed(0.0)
            .pace(0.0)
            .elevationGain(0.0)
            .motionTime(0)
            .pausingTime(0)
            .kilometerSpeeds(List.of())
            .kilometerPaces(List.of())
            .gpsPositions(List.of())
            .build();

        ActivityDbEntity actualActivityDbEntity = mapper.toActivityDbEntity(request, metrics);

        assertNotNull(actualActivityDbEntity.getStartDate());
        assertEquals(Instant.parse("2021-09-10T12:00:00Z"), actualActivityDbEntity.getStartDate());
        assertNotNull(actualActivityDbEntity.getGpsPositions());
        assertTrue(actualActivityDbEntity.getGpsPositions().isEmpty());
    }

    @Test
    void toActivity_withNullInput_returnsNull() {
        assertNull(mapper.toActivity(null));
    }

    @Test
    void toActivities_withNullInput_returnsNull() {
        assertNull(mapper.toActivities(null));
    }

    @Test
    void toActivityDbEntity_withNullInput_returnsNull() {
        assertNull(mapper.toActivityDbEntity(null));
    }
}
