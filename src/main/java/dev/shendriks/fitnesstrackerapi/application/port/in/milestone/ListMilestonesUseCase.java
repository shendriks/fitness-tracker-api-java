package dev.shendriks.fitnesstrackerapi.application.port.in.milestone;

import dev.shendriks.fitnesstrackerapi.domain.entity.Milestone;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

import java.util.List;

public interface ListMilestonesUseCase {
    List<Milestone> getAllMilestonesWithCompletedByUser(UserId userId);
}
