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

/**
 * Application service for querying notifications for a user.
 */
@Service
@AllArgsConstructor
public class NotificationService implements ListAllNotificationsUseCase, ListNewNotificationsUseCase {
    private final ForAccessingNotifications forAccessingNotifications;

    /**
         * Lists all notifications for the user ordered by newest first.
         *
         * @param userId the user identifier
         * @return list of notifications
         */
        public List<Notification> findAllByUser(UserId userId) {
        return forAccessingNotifications.findAllByUser(userId);
    }

    /**
         * Lists notifications for the user that have an ULID greater than the given one.
         * Useful for polling incremental updates.
         *
         * @param userId the user identifier
         * @param ulid the lower ULID bound (exclusive)
         * @return list of notifications newer than the provided ULID
         */
        public List<Notification> findAllByUserSince(UserId userId, NotificationUlid ulid) {
        return forAccessingNotifications.findByUserSinceUlid(userId, ulid);
    }
}
