package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.port.in.notification.ListAllNotificationsUseCase;
import dev.shendriks.fitnesstrackerapi.application.port.in.notification.ListNewNotificationsUseCase;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingNotifications;
import dev.shendriks.fitnesstrackerapi.domain.entity.Notification;
import dev.shendriks.fitnesstrackerapi.domain.value.NotificationUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService implements ListAllNotificationsUseCase, ListNewNotificationsUseCase {
    private final ForAccessingNotifications forAccessingNotifications;

    public List<Notification> findAllByUser(UserId userId) {
        return forAccessingNotifications.findAllByUser(userId);
    }

    public List<Notification> findAllByUserSince(UserId userId, NotificationUlid ulid) {
        return forAccessingNotifications.findByUserSinceUlid(userId, ulid);
    }
}
