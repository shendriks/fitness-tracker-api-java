package dev.shendriks.fitnesstrackerapi.domain.user.repository;

import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUlid(String ulid);
}
