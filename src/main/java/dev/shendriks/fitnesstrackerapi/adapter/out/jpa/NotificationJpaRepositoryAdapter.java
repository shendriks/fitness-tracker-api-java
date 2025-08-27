package dev.shendriks.fitnesstrackerapi.adapter.out.jpa;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.NotificationDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper.NotificationDbEntityMapper;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.NotificationDbEntityRepository;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.UserRepository;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingNotifications;
import dev.shendriks.fitnesstrackerapi.domain.entity.Notification;
import dev.shendriks.fitnesstrackerapi.domain.value.NotificationCreationData;
import dev.shendriks.fitnesstrackerapi.domain.value.NotificationUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@AllArgsConstructor
@Repository
public class NotificationJpaRepositoryAdapter implements ForAccessingNotifications {
    private final NotificationDbEntityRepository repository;
    private final NotificationDbEntityMapper mapper;
    private final UserRepository userRepository;

    @Override
    public List<Notification> findAllByUser(UserId userId) {
        List<NotificationDbEntity> entities = repository.findAllByUserIdOrderByIdDesc(userId.value());
        return mapper.toNotifications(entities);
    }

    @Override
    public List<Notification> findByUserSinceUlid(UserId user, NotificationUlid ulid) {
        List<NotificationDbEntity> entities = repository.findByUserIdAndUlidGreaterThanOrderByIdDesc(user.value(), ulid.value());
        return mapper.toNotifications(entities);
    }

    @Override
    public Notification save(NotificationCreationData notificationCreationData) {
        UserDbEntity user = userRepository.findById(notificationCreationData.userId().value()).orElseThrow();
        NotificationDbEntity entity = new NotificationDbEntity();
        entity.setUser(user);
        entity.setTitle(notificationCreationData.title());
        entity.setDescription(notificationCreationData.description());
        entity = repository.save(entity);
        return mapper.toNotification(entity);
    }
}
