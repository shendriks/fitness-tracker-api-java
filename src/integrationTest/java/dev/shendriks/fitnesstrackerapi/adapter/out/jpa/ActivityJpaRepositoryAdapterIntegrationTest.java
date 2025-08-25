package dev.shendriks.fitnesstrackerapi.adapter.out.jpa;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ActivityDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.enums.AccountType;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureTestEntityManager
class ActivityJpaRepositoryAdapterIntegrationTest {
    private final ActivityJpaRepositoryAdapter adapter;
    private final TestEntityManager entityManager;

    public ActivityJpaRepositoryAdapterIntegrationTest(
        @Autowired ActivityJpaRepositoryAdapter adapter,
        @Autowired TestEntityManager entityManager
    ) {
        this.adapter = adapter;
        this.entityManager = entityManager;
    }

    private UserId createAndPersistUser() {
        UserDbEntity user = UserDbEntity
            .builder()
            .name("Alice")
            .email("alice@example.com")
            .password("password")
            .authority("ROLE_USER")
            .accountType(AccountType.BASIC)
            .build();
        Long userId = entityManager.persistAndGetId(user, Long.class);
        return new UserId(userId);
    }

    @Test
    void saveAndFindAndCount_withActivityCreationData_endToEnd() {
        UserId userId = createAndPersistUser();

        assertEquals(0L, adapter.countByUser(userId), "Expected activity count to be 0");

        ActivityCreationData activityCreationData1 = ActivityCreationData
            .builder()
            .activityType(ActivityType.RUNNING)
            .duration(1800)
            .calories(500)
            .title("Morning Run")
            .description("Nice run")
            .distance(10000)
            .startDate(Instant.parse("2025-08-20T06:00:00Z"))
            .build();
        ActivityCreationData activityCreationData2 = ActivityCreationData
            .builder()
            .activityType(ActivityType.CYCLING)
            .duration(3600)
            .calories(800)
            .title("Evening Ride")
            .description("Chill ride")
            .distance(25000)
            .startDate(Instant.parse("2025-08-21T18:30:00Z"))
            .build();

        Activity actualActivity1 = adapter.saveForUser(userId, activityCreationData1);
        Activity actualActivity2 = adapter.saveForUser(userId, activityCreationData2);

        assertNotNull(actualActivity1.ulid());
        assertNotNull(actualActivity2.ulid());
        assertEquals(2L, adapter.countByUser(userId), "Expected activity count to be 2");

        List<Activity> actualActivities = adapter.findAllByUser(userId);

        assertEquals(2, actualActivities.size(), "Expected two activities");
        String firstUlid = actualActivities.get(0).ulid().value();
        String secondUlid = actualActivities.get(1).ulid().value();
        assertTrue(firstUlid.compareTo(secondUlid) > 0, "Expected ULIDs to be ordered descending");

        Optional<Activity> actualActivity = adapter.findByUserAndId(userId, actualActivity1.ulid());
        assertTrue(actualActivity.isPresent());
        assertEquals(actualActivity1.id(), actualActivity.get().id());

        adapter.deleteForUser(userId, actualActivity1.ulid());

        assertEquals(1L, adapter.countByUser(userId));
        assertTrue(adapter.findByUserAndId(userId, actualActivity1.ulid()).isEmpty());
    }

    @Test
    void updateForUser_updatesFields_andFindTitleByIdReflects_worksAsExpected() {
        UserId userId = createAndPersistUser();

        ActivityCreationData create = ActivityCreationData
            .builder()
            .activityType(ActivityType.WALKING)
            .duration(900)
            .calories(100)
            .title("Short Walk")
            .description("To the park")
            .distance(1200)
            .startDate(Instant.parse("2025-08-22T09:00:00Z"))
            .build();

        Activity actualActivity = adapter.saveForUser(userId, create);

        assertInstanceOf(ActivityId.class, actualActivity.id());
        assertInstanceOf(ActivityUlid.class, actualActivity.ulid());
        assertEquals(900, actualActivity.duration());
        assertEquals(1200, actualActivity.distance());
        assertEquals(Instant.parse("2025-08-22T09:00:00Z"), actualActivity.startDate());
        assertEquals(ActivityType.WALKING, actualActivity.activityType());
        assertEquals("Short Walk", actualActivity.title());
        assertEquals("To the park", actualActivity.description());

        ActivityDbEntity actualSavedDbEntity = entityManager.find(ActivityDbEntity.class, actualActivity.id().value());
        assertInstanceOf(ActivityDbEntity.class, actualSavedDbEntity);
        assertEquals(actualActivity.id().value(), actualSavedDbEntity.getId());
        assertEquals(ActivityType.WALKING, actualSavedDbEntity.getActivityType());
        assertEquals("Short Walk", actualSavedDbEntity.getTitle());
        assertEquals("To the park", actualSavedDbEntity.getDescription());
        assertEquals(900, actualSavedDbEntity.getDuration());
        assertEquals(1200, actualSavedDbEntity.getDistance());
        assertEquals(Instant.parse("2025-08-22T09:00:00Z"), actualSavedDbEntity.getStartDate());

        ActivityUpdateData update = ActivityUpdateData
            .builder()
            .activityType(ActivityType.SWIMMING)
            .title("Swim Session")
            .description("Pool laps")
            .build();

        Activity actualUpdatedActivity = adapter.updateForUser(userId, actualActivity.ulid(), update);

        assertInstanceOf(ActivityId.class, actualActivity.id());
        assertInstanceOf(ActivityUlid.class, actualActivity.ulid());
        assertEquals(ActivityType.SWIMMING, actualUpdatedActivity.activityType());
        assertEquals("Swim Session", actualUpdatedActivity.title());
        assertEquals("Pool laps", actualUpdatedActivity.description());
        assertEquals(900, actualUpdatedActivity.duration());
        assertEquals(1200, actualUpdatedActivity.distance());
        assertEquals(Instant.parse("2025-08-22T09:00:00Z"), actualUpdatedActivity.startDate());

        ActivityDbEntity actualActivityDbEntity = entityManager.find(ActivityDbEntity.class, actualUpdatedActivity.id().value());
        assertInstanceOf(ActivityDbEntity.class, actualActivityDbEntity);
        assertEquals(actualUpdatedActivity.id().value(), actualActivityDbEntity.getId());
        assertEquals(ActivityType.SWIMMING, actualActivityDbEntity.getActivityType());
        assertEquals("Swim Session", actualActivityDbEntity.getTitle());
        assertEquals("Pool laps", actualActivityDbEntity.getDescription());
        assertEquals(900, actualActivityDbEntity.getDuration());
        assertEquals(1200, actualActivityDbEntity.getDistance());
        assertEquals(Instant.parse("2025-08-22T09:00:00Z"), actualActivityDbEntity.getStartDate());
        
        Optional<String> title = adapter.findTitleById(actualUpdatedActivity.id());
        assertTrue(title.isPresent());
        assertEquals("Swim Session", title.get());
    }

    @Test
    void saveForUser_withUploadDataAndGPSTrack_persistsMetricsAndGps_thenDelete() {
        UserId userId = createAndPersistUser();

        ActivityUploadData upload = ActivityUploadData
            .builder()
            .activityType(ActivityType.RUNNING)
            .title("Trail Run")
            .description("Through the woods")
            .build();

        GPSTrackData gps = GPSTrackData
            .builder()
            .name("Trail Run")
            .gpxTime(Optional.of(Instant.parse("2025-08-23T10:00:00Z")))
            .totalLength(1234.56)
            .duration(789)
            .speed(0)
            .pace(0)
            .elevationGain(0)
            .motionTime(0)
            .pausingTime(0)
            .kilometerSpeeds(List.of())
            .kilometerPaces(List.of())
            .gpsPositions(List.of(
                new GPSPositionData(Instant.parse("2025-08-23T10:00:05Z"), 0.0, 1.0, Optional.of(10.0)),
                new GPSPositionData(Instant.parse("2025-08-23T10:05:05Z"), 0.001, 1.001, Optional.empty())
            ))
            .build();

        Activity actualSavedActivity = adapter.saveForUser(userId, upload, gps);

        assertNotNull(actualSavedActivity.id());
        assertEquals(789, actualSavedActivity.duration());
        assertEquals(1234, actualSavedActivity.distance());
        assertEquals("Trail Run", actualSavedActivity.title());
        assertEquals("Through the woods", actualSavedActivity.description());
        assertEquals(2, actualSavedActivity.gpsPositions().size(), "Expected two GPS positions");
        assertEquals(Instant.parse("2025-08-23T10:00:05Z"), actualSavedActivity.gpsPositions().getFirst().timestamp());
        assertEquals(0.0, actualSavedActivity.gpsPositions().getFirst().latitude());
        assertEquals(1.0, actualSavedActivity.gpsPositions().getFirst().longitude());
        assertEquals(10.0, actualSavedActivity.gpsPositions().getFirst().altitude());
        assertEquals(Instant.parse("2025-08-23T10:05:05Z"), actualSavedActivity.gpsPositions().get(1).timestamp());
        assertEquals(0.001, actualSavedActivity.gpsPositions().get(1).latitude());
        assertEquals(1.001, actualSavedActivity.gpsPositions().get(1).longitude());
        assertNull(actualSavedActivity.gpsPositions().get(1).altitude());

        ActivityDbEntity actualActivityDbEntity = entityManager.find(ActivityDbEntity.class, actualSavedActivity.id().value());

        assertInstanceOf(ActivityDbEntity.class, actualActivityDbEntity);
        assertEquals(actualSavedActivity.id().value(), actualActivityDbEntity.getId());
        assertEquals(ActivityType.RUNNING, actualActivityDbEntity.getActivityType());
        assertEquals("Trail Run", actualActivityDbEntity.getTitle());
        assertEquals("Through the woods", actualActivityDbEntity.getDescription());
        assertEquals(789, actualActivityDbEntity.getDuration());
        assertEquals(1234, actualActivityDbEntity.getDistance());
        assertEquals(2, actualActivityDbEntity.getGpsPositions().size(), "Expected two GPS positions");
        assertEquals(Instant.parse("2025-08-23T10:00:05Z"), actualActivityDbEntity.getGpsPositions().getFirst().getTimestamp());
        assertEquals(0.0, actualActivityDbEntity.getGpsPositions().getFirst().getLatitude());
        assertEquals(1.0, actualActivityDbEntity.getGpsPositions().getFirst().getLongitude());
        assertEquals(10.0, actualActivityDbEntity.getGpsPositions().getFirst().getAltitude());
        assertEquals(Instant.parse("2025-08-23T10:05:05Z"), actualActivityDbEntity.getGpsPositions().get(1).getTimestamp());
        assertEquals(0.001, actualActivityDbEntity.getGpsPositions().get(1).getLatitude());
        assertEquals(1.001, actualActivityDbEntity.getGpsPositions().get(1).getLongitude());
        assertNull(actualActivityDbEntity.getGpsPositions().get(1).getAltitude());
    }
}
