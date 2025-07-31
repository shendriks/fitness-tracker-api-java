package dev.shendriks.fitnesstrackerapi.domain.notification.mapper;

import dev.shendriks.fitnesstrackerapi.domain.notification.dto.NotificationResponse;
import dev.shendriks.fitnesstrackerapi.domain.notification.entity.Notification;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationMapper {
    public List<NotificationResponse> toResponses(Iterable<Notification> notifications) {
        List<NotificationResponse> milestoneResponses = new ArrayList<>();
        for (Notification notification : notifications) {
            milestoneResponses.add(toResponse(notification));
        }

        return milestoneResponses;
    }

    public NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
            notification.getUlid(),
            notification.getTitle(),
            notification.getDescription(),
            notification.getCreatedAt()
        );
    }
}
