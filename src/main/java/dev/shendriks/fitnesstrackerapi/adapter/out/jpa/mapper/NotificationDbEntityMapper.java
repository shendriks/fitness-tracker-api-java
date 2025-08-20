package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.NotificationDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationDbEntityMapper {
    @Mapping(target = "id", expression = "java(new NotificationId(entity.getId()))")
    @Mapping(target = "ulid", expression = "java(new NotificationUlid(entity.getUlid()))")
    Notification toNotification(NotificationDbEntity entity);

    List<Notification> toNotifications(List<NotificationDbEntity> entities);
}
