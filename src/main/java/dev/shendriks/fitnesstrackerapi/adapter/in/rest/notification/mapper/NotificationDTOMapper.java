package dev.shendriks.fitnesstrackerapi.adapter.in.rest.notification.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.notification.dto.NotificationResponseDTO;
import dev.shendriks.fitnesstrackerapi.domain.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationDTOMapper {
    @Mapping(target = "id", source = "ulid.value")
    NotificationResponseDTO toNotificationResponseDTO(Notification notification);

    @Mapping(target = "id", source = "ulid.value")
    List<NotificationResponseDTO> toNotificationResponseDTOs(List<Notification> notifications);
}
