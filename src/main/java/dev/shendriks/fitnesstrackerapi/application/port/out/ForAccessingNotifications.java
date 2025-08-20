package dev.shendriks.fitnesstrackerapi.application.port.out;

import dev.shendriks.fitnesstrackerapi.domain.entity.Notification;
import dev.shendriks.fitnesstrackerapi.domain.value.NotificationCreationData;
import dev.shendriks.fitnesstrackerapi.domain.value.NotificationUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

import java.util.List;

public interface ForAccessingNotifications {
    List<Notification> findAllByUser(UserId userId);

    List<Notification> findByUserSinceUlid(UserId user, NotificationUlid ulid);

    Notification save(NotificationCreationData notificationCreaionData);
}
