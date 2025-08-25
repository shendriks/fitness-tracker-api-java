package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingNotifications;
import dev.shendriks.fitnesstrackerapi.domain.entity.Notification;
import dev.shendriks.fitnesstrackerapi.domain.value.NotificationId;
import dev.shendriks.fitnesstrackerapi.domain.value.NotificationUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class NotificationServiceTest {
    private ForAccessingNotifications forAccessingNotifications;
    private NotificationService service;

    private static Notification buildNotification(long id, String ulid) {
        return Notification
            .builder()
            .id(new NotificationId(id))
            .ulid(new NotificationUlid(ulid))
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .title("Title")
            .description("Description")
            .build();
    }

    @BeforeEach
    void setUp() {
        forAccessingNotifications = mock(ForAccessingNotifications.class);
        service = new NotificationService(forAccessingNotifications);
    }

    @Test
    void findAllByUser_returnsNotifications_andDelegatesToPort() {
        UserId userId = new UserId(42L);
        List<Notification> notifications = List.of(
            buildNotification(1L, "TESTULID000000000000000001"),
            buildNotification(2L, "TESTULID000000000000000002")
        );
        when(forAccessingNotifications.findAllByUser(userId)).thenReturn(notifications);

        List<Notification> actualNotifications = service.findAllByUser(userId);

        assertEquals(notifications, actualNotifications);
        verify(forAccessingNotifications).findAllByUser(userId);
        verifyNoMoreInteractions(forAccessingNotifications);
    }

    @Test
    void findAllByUserSince_returnsNotifications_andDelegatesToPort() {
        UserId userId = new UserId(42L);
        NotificationUlid sinceUlid = new NotificationUlid("TESTULID000000000000000003");
        List<Notification> notifications = List.of(buildNotification(4L, "TESTULID000000000000000004"));
        when(forAccessingNotifications.findByUserSinceUlid(userId, sinceUlid)).thenReturn(notifications);

        List<Notification> actualNotifications = service.findAllByUserSince(userId, sinceUlid);

        assertEquals(notifications, actualNotifications);
        verify(forAccessingNotifications).findByUserSinceUlid(userId, sinceUlid);
        verifyNoMoreInteractions(forAccessingNotifications);
    }
}
