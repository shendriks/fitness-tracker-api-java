package dev.shendriks.fitnesstrackerapi.application;

import dev.shendriks.fitnesstrackerapi.developer.Developer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends CrudRepository<Application, Long> {
    boolean existsByName(String name);
    List<Application> findByDeveloperOrderByIdDesc(Developer developer);

    Optional<Application> findByApiKey(String apiKey);
}
