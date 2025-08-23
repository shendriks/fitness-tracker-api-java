package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.NotificationDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.Notification;
import dev.shendriks.fitnesstrackerapi.domain.value.NotificationId;
import dev.shendriks.fitnesstrackerapi.domain.value.NotificationUlid;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationDbEntityMapperTest {
    private final NotificationDbEntityMapper mapper = Mappers.getMapper(NotificationDbEntityMapper.class);

    @Test
    void toNotification_shouldMapAllFields_andWrapIdTypes() {
        NotificationDbEntity entity = NotificationDbEntity
            .builder()
            .id(42L)
            .ulid("TESTULID000000000000000001")
            .user(UserDbEntity.builder().id(1L).build())
            .createdAt(Instant.parse("2025-08-22T10:15:30Z"))
            .updatedAt(Instant.parse("2025-08-22T11:15:30Z"))
            .title("Welcome")
            .description("Your account was created")
            .build();

        Notification actualNotification = mapper.toNotification(entity);

        assertInstanceOf(Notification.class, actualNotification);
        assertEquals(new NotificationId(42L), actualNotification.id());
        assertEquals(new NotificationUlid("TESTULID000000000000000001"), actualNotification.ulid());
        assertEquals("Welcome", actualNotification.title());
        assertEquals("Your account was created", actualNotification.description());
        assertEquals(Instant.parse("2025-08-22T10:15:30Z"), actualNotification.createdAt());
        assertEquals(Instant.parse("2025-08-22T11:15:30Z"), actualNotification.updatedAt());
    }

    @Test
    void toNotifications_shouldMapList() {
        List<NotificationDbEntity> entities = List.of(
            NotificationDbEntity
                .builder()
                .id(1L)
                .ulid("TESTULID000000000000000002")
                .user(UserDbEntity.builder().id(1L).build())
                .createdAt(Instant.parse("2025-08-22T00:00:00Z"))
                .updatedAt(Instant.parse("2025-08-22T01:00:00Z"))
                .title("A")
                .description("Desc A")
                .build(),
            NotificationDbEntity
                .builder()
                .id(2L)
                .ulid("TESTULID000000000000000003")
                .user(UserDbEntity.builder().id(1L).build())
                .createdAt(Instant.parse("2025-08-23T00:00:00Z"))
                .updatedAt(Instant.parse("2025-08-23T01:00:00Z"))
                .title("B")
                .description("Desc B")
                .build()
        );

        List<Notification> actualNotifications = mapper.toNotifications(entities);

        assertEquals(2, actualNotifications.size());
        assertEquals(new NotificationId(1L), actualNotifications.getFirst().id());
        assertEquals(new NotificationUlid("TESTULID000000000000000002"), actualNotifications.getFirst().ulid());
        assertEquals("A", actualNotifications.getFirst().title());
        assertEquals(Instant.parse("2025-08-22T00:00:00Z"), actualNotifications.getFirst().createdAt());
        assertEquals(Instant.parse("2025-08-22T01:00:00Z"), actualNotifications.getFirst().updatedAt());
        assertEquals(new NotificationId(2L), actualNotifications.get(1).id());
        assertEquals(new NotificationUlid("TESTULID000000000000000003"), actualNotifications.get(1).ulid());
        assertEquals("B", actualNotifications.get(1).title());
        assertEquals(Instant.parse("2025-08-23T00:00:00Z"), actualNotifications.get(1).createdAt());
        assertEquals(Instant.parse("2025-08-23T01:00:00Z"), actualNotifications.get(1).updatedAt());
    }

    @Test
    void toNotification_withNullInput_returnsNull() {
        assertNull(mapper.toNotification(null));
    }

    @Test
    void toNotifications_withNullInput_returnsNull() {
        assertNull(mapper.toNotifications(null));
    }

    @Test
    void toNotifications_withEmptyList_returnsEmptyList() {
        List<Notification> actualNotifications = mapper.toNotifications(List.of());
        assertEquals(List.of(), actualNotifications);
    }
}
