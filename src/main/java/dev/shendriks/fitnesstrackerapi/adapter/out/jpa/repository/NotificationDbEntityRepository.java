package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.NotificationDbEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationDbEntityRepository extends JpaRepository<NotificationDbEntity, Long> {
    List<NotificationDbEntity> findAllByUserIdOrderByIdDesc(Long user);

    List<NotificationDbEntity> findByUserIdAndUlidGreaterThanOrderByIdDesc(Long user, String ulid);
}

