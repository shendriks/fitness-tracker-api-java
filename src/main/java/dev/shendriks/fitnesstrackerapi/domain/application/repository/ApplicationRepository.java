package dev.shendriks.fitnesstrackerapi.domain.application.repository;

import dev.shendriks.fitnesstrackerapi.domain.application.entity.Application;
import dev.shendriks.fitnesstrackerapi.domain.developer.entity.Developer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends CrudRepository<Application, Long> {
    boolean existsByName(String name);
    List<Application> findByDeveloperOrderByIdDesc(Developer developer);

    Optional<Application> findByApiKey(String apiKey);
}
