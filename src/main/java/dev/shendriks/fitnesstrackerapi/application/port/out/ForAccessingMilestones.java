package dev.shendriks.fitnesstrackerapi.application.port.out;

import dev.shendriks.fitnesstrackerapi.domain.entity.Milestone;
import dev.shendriks.fitnesstrackerapi.domain.value.MilestoneId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

import java.util.List;
import java.util.Optional;

public interface ForAccessingMilestones {
    List<Milestone> findAllWithCompletedByUser(UserId userId);

    Optional<String> findNameById(MilestoneId milestoneId);
}
