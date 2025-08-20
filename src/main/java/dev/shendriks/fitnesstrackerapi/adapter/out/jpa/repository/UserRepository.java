package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserDbEntity, Long> {
    Optional<UserDbEntity> findByEmail(String email);

    Optional<UserDbEntity> findByUlid(String ulid);

    long countByEmail(String email);
}
