package dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity.*;
import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ActivityDTOMapperTest {
    private final ActivityDTOMapper mapper = Mappers.getMapper(ActivityDTOMapper.class);

    private static Activity buildActivity(String ulid) {
        return buildActivity(ulid, List.of());
    }

    private static Activity buildActivity(String ulid, List<GPSPosition> positions) {
        return Activity
            .builder()
            .id(new ActivityId(1L))
            .ulid(new ActivityUlid(ulid))
            .activityType(ActivityType.MOUNTAIN_BIKING)
            .duration(7200)
            .calories(1200)
            .createdAt(Instant.parse("2025-08-22T00:00:00Z"))
            .updatedAt(Instant.parse("2025-08-22T00:00:00Z"))
            .title("Sample Title")
            .description("Sample Description")
            .distance(12345)
            .startDate(Instant.parse("2025-08-22T00:00:00Z"))
            .gpsPositions(positions)
            .build();
    }

    @Test
    void toActivityResponse_shouldMapAllSimpleFieldsAndUlidToId() {
        Activity activity = buildActivity("TESTULID000000000000000000");

        ActivityResponseDTO dto = mapper.toActivityResponse(activity);

        assertNotNull(dto);
        assertEquals("TESTULID000000000000000000", dto.id());
        assertEquals(activity.activityType(), dto.activityType());
        assertEquals(activity.duration(), dto.duration());
        assertEquals(activity.calories(), dto.calories());
        assertEquals(activity.createdAt(), dto.createdAt());
        assertEquals(activity.updatedAt(), dto.updatedAt());
        assertEquals(activity.title(), dto.title());
        assertEquals(activity.description(), dto.description());
        assertEquals(activity.distance(), dto.distance());
        assertEquals(activity.startDate(), dto.startDate());
    }

    @Test
    void toActivityResponses_shouldMapList() {
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
    void toActivityDetailsResponse_shouldMapGpsPositions_subsetFields() {
        GPSPosition position1 = GPSPosition
            .builder()
            .timestamp(Instant.parse("2025-08-22T00:00:00Z"))
            .latitude(0.0)
            .longitude(0.0)
            .altitude(0.0)
            .accuracy(5.0)
            .build();
        GPSPosition position2 = GPSPosition
            .builder()
            .timestamp(Instant.parse("2025-08-22T00:05:00Z"))
            .latitude(0.2)
            .longitude(0.1)
            .altitude(0.0)
            .accuracy(6.0)
            .build();
        Activity activity = buildActivity("TESTULID000000000000000003", List.of(position1, position2));

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
    void toActivityCreationData_shouldConvertActivityTypeFromString() {
        ActivityCreateRequestDTO request = ActivityCreateRequestDTO
            .builder()
            .activityType("running")
            .duration(3600)
            .calories(900)
            .title("Morning Run")
            .description("Nice run")
            .distance(10000)
            .startDate(Instant.parse("2024-05-10T06:00:00Z"))
            .build();

        ActivityCreationData activityCreationData = mapper.toActivityCreationData(request);

        assertNotNull(activityCreationData);
        assertEquals(ActivityType.RUNNING, activityCreationData.activityType());
        assertEquals(3600, activityCreationData.duration());
        assertEquals(900, activityCreationData.calories());
        assertEquals("Morning Run", activityCreationData.title());
        assertEquals("Nice run", activityCreationData.description());
        assertEquals(10000, activityCreationData.distance());
        assertEquals(Instant.parse("2024-05-10T06:00:00Z"), activityCreationData.startDate());
    }

    @Test
    void toActivityUpdateData_shouldConvertActivityTypeFromString_caseInsensitive() {
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
    void toActivityUploadData_shouldMapMultipartFile() {
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
}
