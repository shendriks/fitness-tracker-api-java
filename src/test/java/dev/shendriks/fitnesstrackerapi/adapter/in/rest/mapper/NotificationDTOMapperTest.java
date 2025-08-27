package dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.NotificationResponseDTO;
import dev.shendriks.fitnesstrackerapi.domain.entity.Notification;
import dev.shendriks.fitnesstrackerapi.domain.value.NotificationId;
import dev.shendriks.fitnesstrackerapi.domain.value.NotificationUlid;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationDTOMapperTest {
    private final NotificationDTOMapper mapper = Mappers.getMapper(NotificationDTOMapper.class);

    private static Notification buildNotification(String ulid, String title, String description, Instant createdAt) {
        return Notification
            .builder()
            .id(new NotificationId(1L))
            .ulid(new NotificationUlid(ulid))
            .createdAt(createdAt)
            .updatedAt(createdAt)
            .title(title)
            .description(description)
            .build();
    }

    @Test
    void toNotificationResponseDTO_mapsAllFields() {
        Instant createdAt = Instant.parse("2025-08-22T10:15:30Z");
        Notification notification = buildNotification(
            "TESTULID000000000000000001",
            "Welcome",
            "Your account was created",
            createdAt
        );

        NotificationResponseDTO dto = mapper.toNotificationResponseDTO(notification);

        assertNotNull(dto);
        assertEquals("TESTULID000000000000000001", dto.id());
        assertEquals("Welcome", dto.title());
        assertEquals("Your account was created", dto.description());
        assertEquals(createdAt, dto.createdAt());
    }

    @Test
    void toNotificationResponseDTOs_mapsList() {
        var notifications = List.of(
            buildNotification("TESTULID000000000000000002", "A", "Desc A", Instant.parse("2025-08-22T00:00:00Z")),
            buildNotification("TESTULID000000000000000003", "B", "Desc B", Instant.parse("2025-08-23T00:00:00Z"))
        );

        List<NotificationResponseDTO> dtos = mapper.toNotificationResponseDTOs(notifications);

        assertEquals(2, dtos.size());
        assertEquals("TESTULID000000000000000002", dtos.get(0).id());
        assertEquals("TESTULID000000000000000003", dtos.get(1).id());
        assertEquals("A", dtos.get(0).title());
        assertEquals("B", dtos.get(1).title());
        assertEquals(Instant.parse("2025-08-22T00:00:00Z"), dtos.get(0).createdAt());
        assertEquals(Instant.parse("2025-08-23T00:00:00Z"), dtos.get(1).createdAt());
    }

    @Test
    void toNotificationResponseDTOs_withEmptyList_returnsEmptyList() {
        List<NotificationResponseDTO> dtos = mapper.toNotificationResponseDTOs(List.of());
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    void toNotificationResponseDTO_withNullInput_returnsNull() {
        assertNull(mapper.toNotificationResponseDTO(null));
    }

    @Test
    void toNotificationResponseDTOs_withNullInput_returnsNull() {
        assertNull(mapper.toNotificationResponseDTOs(null));
    }
}
