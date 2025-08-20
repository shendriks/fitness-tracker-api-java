package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.port.in.milestone.ListMilestonesUseCase;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingMilestones;
import dev.shendriks.fitnesstrackerapi.domain.entity.Milestone;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MilestoneService implements ListMilestonesUseCase {
    private final ForAccessingMilestones forAccessingMilestones;

    @Override
    public List<Milestone> getAllMilestonesWithCompletedByUser(UserId userId) {
        return forAccessingMilestones.findAllWithCompletedByUser(userId);
    }
}
