package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.port.in.milestone.ListMilestonesUseCase;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingMilestones;
import dev.shendriks.fitnesstrackerapi.domain.entity.Milestone;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service exposing milestone queries.
 *
 * <p>Delegates to the ForAccessingMilestones port to fetch milestones including
 * whether each is completed by the given user.</p>
 */
@Service
@AllArgsConstructor
public class MilestoneService implements ListMilestonesUseCase {
    private final ForAccessingMilestones forAccessingMilestones;

    /**
     * Lists all milestones for the user and indicates whether each is completed.
     *
     * @param userId the user identifier
     * @return the list of milestones with completion status
     */
    @Override
    public List<Milestone> getAllMilestonesWithCompletedByUser(UserId userId) {
        return forAccessingMilestones.findAllWithCompletedByUser(userId);
    }
}
