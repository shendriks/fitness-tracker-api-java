package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.port.in.challengeparticipation.ListChallengeParticipationsUseCase;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingChallengeParticipations;
import dev.shendriks.fitnesstrackerapi.domain.entity.ChallengeParticipation;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service for listing a user's current challenge participations.
 */
@Service
@AllArgsConstructor
public class ChallengeParticipationService implements ListChallengeParticipationsUseCase {
    private final ForAccessingChallengeParticipations forAccessingChallengeParticipations;

    /**
     * Returns current challenge participations for the user.
     *
     * @param userId the user identifier
     * @return list of participations
     */
    @Override
    public List<ChallengeParticipation> getAllChallengeParticipations(UserId userId) {
        return forAccessingChallengeParticipations.findCurrentByUser(userId);
    }
}
