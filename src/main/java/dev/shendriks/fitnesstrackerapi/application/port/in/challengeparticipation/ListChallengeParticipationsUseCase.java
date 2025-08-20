package dev.shendriks.fitnesstrackerapi.application.port.in.challengeparticipation;

import dev.shendriks.fitnesstrackerapi.domain.entity.ChallengeParticipation;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

import java.util.List;

public interface ListChallengeParticipationsUseCase {
    List<ChallengeParticipation> getAllChallengeParticipations(UserId userId);
}
