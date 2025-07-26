package dev.shendriks.fitnesstrackerapi.domain.milestone.repository;

import dev.shendriks.fitnesstrackerapi.domain.milestone.entity.Milestone;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MilestoneRepository extends CrudRepository<Milestone, Long> {
    Iterable<Milestone> findByIdNotIn(List<Long> milestoneIds);
}
