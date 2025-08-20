package dev.shendriks.fitnesstrackerapi.adapter.out.jpa;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper.MilestoneDbEntityMapper;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection.MilestoneProjection;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.MilestoneDbEntityRepository;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingMilestones;
import dev.shendriks.fitnesstrackerapi.domain.entity.Milestone;
import dev.shendriks.fitnesstrackerapi.domain.value.MilestoneId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class MilestoneJpaRepositioryAdapter implements ForAccessingMilestones {
    private final MilestoneDbEntityRepository repository;
    private final MilestoneDbEntityMapper mapper;

    @Override
    public List<Milestone> findAllWithCompletedByUser(UserId userId) {
        List<MilestoneProjection> milestoneProjections = repository.findAllWithCompletedByUser(userId.value());
        return mapper.toMilestones(milestoneProjections);
    }

    @Override
    public Optional<String> findNameById(MilestoneId milestoneId) {
        return repository.findNameById(milestoneId.getValue());
    }
}
