package dev.shendriks.fitnesstrackerapi.application.port.in.notification;

import dev.shendriks.fitnesstrackerapi.domain.entity.Notification;
import dev.shendriks.fitnesstrackerapi.domain.value.NotificationUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

import java.util.List;

public interface ListNewNotificationsUseCase {
    List<Notification> findAllByUserSince(UserId userId, NotificationUlid ulid);
}
