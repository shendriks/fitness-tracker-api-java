package dev.shendriks.fitnesstrackerapi.domain.notification.repository;

import dev.shendriks.fitnesstrackerapi.domain.notification.entity.Notification;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface NotificationRepository extends CrudRepository<Notification, Long> {
    Iterable<Notification> findAllByUserOrderByIdDesc(User user);

    Iterable<Notification> findByUserAndIdGreaterThanOrderByIdDesc(User user, Long id);

    Optional<Notification> findByUlid(String ulid);
}
