package dev.shendriks.fitnesstrackerapi.domain.developer.repository;

import dev.shendriks.fitnesstrackerapi.domain.developer.entity.Developer;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DeveloperRepository extends CrudRepository<Developer, Long> {
    Optional<Developer> findByEmail(String email);
    Optional<Developer> findByUlid(String ulid);
}
