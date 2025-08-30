package dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity.*;
import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.entity.ActivityDetails;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import dev.shendriks.fitnesstrackerapi.infrastructure.Constant;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActivityDTOMapperTest {
    private final ActivityDTOMapper mapper = Mappers.getMapper(ActivityDTOMapper.class);

    private static Activity buildActivity(String ulid) {
        return Activity
            .builder()
            .id(new ActivityId(1L))
            .ulid(new ActivityUlid(ulid))
            .activityType(ActivityType.MOUNTAIN_BIKING)
            .duration(Duration.ofSeconds(7200L))
            .distance(Distance.ofMeters(12345.0))
            .averageSpeed(Speed.ofMetersPerSecond(7200 / 12345.0))
            .createdAt(Instant.parse("2025-08-22T00:00:00Z"))
            .updatedAt(Instant.parse("2025-08-22T00:00:00Z"))
            .title("Sample Title")
            .description("Sample Description")
            .startDate(Instant.parse("2025-08-22T00:00:00Z"))
            .build();
    }

    private static ActivityDetails buildActivityDetails(String ulid, List<GPSPosition> positions) {
        return ActivityDetails
            .builder()
            .id(new ActivityId(1L))
            .ulid(new ActivityUlid(ulid))
            .activityType(ActivityType.MOUNTAIN_BIKING)
            .duration(Duration.ofSeconds(7200L))
            .distance(Distance.ofMeters(12345.0))
            .averageSpeed(Speed.ofMetersPerSecond(7200 / 12345.0))
            .createdAt(Instant.parse("2025-08-22T00:00:00Z"))
            .updatedAt(Instant.parse("2025-08-22T00:00:00Z"))
            .title("Sample Title")
            .description("Sample Description")
            .startDate(Instant.parse("2025-08-22T00:00:00Z"))
            .gpsPositions(positions)
            .build();
    }

    @Test
    void toActivityResponse_mapsAllFieldsAndUlidToId() {
        Activity activity = buildActivity("TESTULID000000000000000000");

        ActivityResponseDTO dto = mapper.toActivityResponse(activity);

        assertNotNull(dto);
        assertEquals("TESTULID000000000000000000", dto.id());
        assertEquals(activity.activityType(), dto.activityType());
        assertEquals(activity.duration().toSeconds(), dto.duration(), Constant.EPSILON);
        assertEquals(activity.distance().toMeters(), dto.distance(), Constant.EPSILON);
        assertEquals(activity.createdAt(), dto.createdAt());
        assertEquals(activity.updatedAt(), dto.updatedAt());
        assertEquals(activity.title(), dto.title());
        assertEquals(activity.description(), dto.description());
        assertEquals(activity.startDate(), dto.startDate());
    }

    @Test
    void toActivityResponses_mapsList() {
        var activities = List.of(
            buildActivity("TESTULID000000000000000001"),
            buildActivity("TESTULID000000000000000002")
        );

        List<ActivityResponseDTO> list = mapper.toActivityResponses(activities);

        assertEquals(2, list.size());
        assertEquals("TESTULID000000000000000001", list.get(0).id());
        assertEquals("TESTULID000000000000000002", list.get(1).id());
    }

    @Test
    void toActivityDetailsResponse_mapsGpsPositions() {
        GPSPosition position1 = GPSPosition
            .builder()
            .timestamp(Instant.parse("2025-08-22T00:00:00Z"))
            .latitude(0.0)
            .longitude(0.0)
            .altitude(0.0)
            .build();
        GPSPosition position2 = GPSPosition
            .builder()
            .timestamp(Instant.parse("2025-08-22T00:05:00Z"))
            .latitude(0.2)
            .longitude(0.1)
            .altitude(0.0)
            .build();
        ActivityDetails activity = buildActivityDetails("TESTULID000000000000000003", List.of(position1, position2));

        ActivityDetailsResponseDTO activityDetailsResponseDTO = mapper.toActivityDetailsResponse(activity);

        assertNotNull(activityDetailsResponseDTO);
        assertEquals("TESTULID000000000000000003", activityDetailsResponseDTO.id());
        assertNotNull(activityDetailsResponseDTO.gpsPositions());
        assertEquals(2, activityDetailsResponseDTO.gpsPositions().size());
        GPSPositionResponseDTO gpsPositionResponseDTO1 = activityDetailsResponseDTO.gpsPositions().getFirst();
        assertEquals(position1.timestamp(), gpsPositionResponseDTO1.timestamp());
        assertEquals(position1.latitude(), gpsPositionResponseDTO1.latitude());
        assertEquals(position1.longitude(), gpsPositionResponseDTO1.longitude());
        GPSPositionResponseDTO gpsPositionResponseDTO2 = activityDetailsResponseDTO.gpsPositions().get(1);
        assertEquals(position2.timestamp(), gpsPositionResponseDTO2.timestamp());
        assertEquals(position2.latitude(), gpsPositionResponseDTO2.latitude());
        assertEquals(position2.longitude(), gpsPositionResponseDTO2.longitude());
    }

    @Test
    void toActivityCreationData_convertsActivityTypeFromString() {
        ActivityCreateRequestDTO request = ActivityCreateRequestDTO
            .builder()
            .activityType("running")
            .duration(3600L)
            .distance(10000.0)
            .calories(900)
            .title("Morning Run")
            .description("Nice run")
            .startDate(Instant.parse("2024-05-10T06:00:00Z"))
            .build();

        ActivityCreationData activityCreationData = mapper.toActivityCreationData(request);

        assertNotNull(activityCreationData);
        assertEquals(ActivityType.RUNNING, activityCreationData.activityType());
        assertEquals(3600.0, activityCreationData.duration().toSeconds(), Constant.EPSILON);
        assertEquals(10000.0, activityCreationData.distance().toMeters(), Constant.EPSILON);
        assertEquals(900, activityCreationData.calories());
        assertEquals("Morning Run", activityCreationData.title());
        assertEquals("Nice run", activityCreationData.description());
        assertEquals(Instant.parse("2024-05-10T06:00:00Z"), activityCreationData.startDate());
    }

    @Test
    void toActivityUpdateData_convertsActivityTypeFromStringCaseInsensitive() {
        ActivityUpdateRequestDTO request = ActivityUpdateRequestDTO
            .builder()
            .activityType("walking")
            .title("Evening Walk")
            .description("Relaxing")
            .build();

        ActivityUpdateData activityUpdateData = mapper.toActivityUpdateData(request);

        assertNotNull(activityUpdateData);
        assertEquals(ActivityType.WALKING, activityUpdateData.activityType());
        assertEquals("Evening Walk", activityUpdateData.title());
        assertEquals("Relaxing", activityUpdateData.description());
    }

    @Test
    void toActivityUploadData_mapsMultipartFile() {
        ActivityUploadRequestDTO request = ActivityUploadRequestDTO
            .builder()
            .activityType("cycling")
            .title("Commute")
            .description("To work")
            .gpxFile(new MockMultipartFile(
                "gpxFile",
                "track.gpx",
                "application/gpx+xml",
                "<gpx></gpx>".getBytes()
            ))
            .build();

        ActivityUploadData data = mapper.toActivityUploadData(request);

        assertNotNull(data);
        assertEquals(ActivityType.CYCLING, data.activityType());
        assertEquals("Commute", data.title());
        assertEquals("To work", data.description());
        assertNotNull(data.gpxFile());
        assertEquals("track.gpx", data.gpxFile().getOriginalFilename());
    }

    @Test
    void toActivityResponse_withNullInput_returnsNull() {
        assertNull(mapper.toActivityResponse(null));
    }

    @Test
    void toActivityResponses_withNullInput_returnsNull() {
        assertNull(mapper.toActivityResponses(null));
    }

    @Test
    void toActivityDetailsResponse_withNullInput_returnsNull() {
        assertNull(mapper.toActivityDetailsResponse(null));
    }

    @Test
    void toActivityCreationData_withNullInput_returnsNull() {
        assertNull(mapper.toActivityCreationData(null));
    }

    @Test
    void toActivityUpdateData_withNullInput_returnsNull() {
        assertNull(mapper.toActivityUpdateData(null));
    }

    @Test
    void toActivityUploadData_withNullInput_returnsNull() {
        assertNull(mapper.toActivityUploadData(null));
    }
}
