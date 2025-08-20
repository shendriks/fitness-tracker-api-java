package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ActivityDbEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ActivityDbEntityRepository extends
    CrudRepository<ActivityDbEntity, Long>,
    PagingAndSortingRepository<ActivityDbEntity, Long> {
    List<ActivityDbEntity> findAllByUserIdOrderByUlidDesc(Long userId);

    long countByUserId(Long userId);

    Optional<ActivityDbEntity> findByUserIdAndUlid(Long userId, String ulid);

    @Query("SELECT a.title FROM ActivityDbEntity a WHERE a.id = :id")
    Optional<String> findTitleById(@Param("id") Long id);
}
