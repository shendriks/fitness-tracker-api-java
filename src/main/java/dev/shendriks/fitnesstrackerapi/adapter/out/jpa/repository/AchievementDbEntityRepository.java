package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.AchievementDbEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementDbEntityRepository extends JpaRepository<AchievementDbEntity, Long> {
}
