package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.NotificationDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.Notification;
import dev.shendriks.fitnesstrackerapi.domain.value.NotificationId;
import dev.shendriks.fitnesstrackerapi.domain.value.NotificationUlid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", imports = {NotificationId.class, NotificationUlid.class})
public interface NotificationDbEntityMapper {
    @Mapping(target = "id", expression = "java(new NotificationId(entity.getId()))")
    @Mapping(target = "ulid", expression = "java(new NotificationUlid(entity.getUlid()))")
    Notification toNotification(NotificationDbEntity entity);

    List<Notification> toNotifications(List<NotificationDbEntity> entities);
}
