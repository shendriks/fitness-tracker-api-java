package dev.shendriks.fitnesstrackerapi.domain.notification.exception;


import dev.shendriks.fitnesstrackerapi.domain.exception.ObjectNotFoundException;

public class NotificationNotFoundException extends ObjectNotFoundException {
    public NotificationNotFoundException(String id) {
        super("Notification with id " + id + " not found");
    }
}
