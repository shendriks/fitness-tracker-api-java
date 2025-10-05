package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.event.ChallengeJoinedEvent;
import dev.shendriks.fitnesstrackerapi.application.event.ChallengeLeftEvent;
import dev.shendriks.fitnesstrackerapi.application.exception.ChallengeNotFoundException;
import dev.shendriks.fitnesstrackerapi.application.port.in.challenge.JoinChallengeUseCase;
import dev.shendriks.fitnesstrackerapi.application.port.in.challenge.LeaveChallengeUseCase;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingChallengeParticipations;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingChallenges;
import dev.shendriks.fitnesstrackerapi.domain.entity.ChallengeParticipation;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Application service handling user participation in challenges.
 *
 * <p>Validates existence, ensures idempotency for join/leave operations,
 * publishes events, and updates related trophies/completions.</p>
 */
@Service
@AllArgsConstructor
@Transactional
public class ChallengeJoinLeaveService implements LeaveChallengeUseCase, JoinChallengeUseCase {
    private final ApplicationEventPublisher eventPublisher;
    private final ForAccessingChallenges forAccessingChallenges;
    private final ForAccessingChallengeParticipations forAccessingChallengeParticipations;
    private final ChallengeCompletionUpdateService challengeCompletionUpdateService;
    private final TrophyManagementService trophyManagementService;

    /**
     * Adds the user to the specified challenge, publishing a ChallengeJoinedEvent and updating completion.
     * No-op if the user is already a participant.
     * 
     * @param userId the user to join
     * @param challengeUlid the challenge identifier
     * @throws ChallengeNotFoundException if the challenge does not exist
     */
    @Override
    public void joinChallenge(UserId userId, ChallengeUlid challengeUlid) {
        if (!forAccessingChallenges.existsByUlid(challengeUlid)) {
            throw new ChallengeNotFoundException(challengeUlid.getValue());
        }

        if (forAccessingChallengeParticipations.existsByChallengeAndUser(userId, challengeUlid)) {
            return;
        }

        ChallengeParticipation challengeParticipation = forAccessingChallengeParticipations.joinChallenge(userId, challengeUlid);
        eventPublisher.publishEvent(new ChallengeJoinedEvent(userId, challengeUlid));
        challengeCompletionUpdateService.updateChallengeCompletion(challengeParticipation);
    }

    /**
     * Removes the user from the specified challenge, publishing a ChallengeLeftEvent
     * and deleting any associated trophy if necessary. No-op if the user is not a participant.
     * 
     * @param userId the user to remove
     * @param challengeUlid the challenge identifier
     * @throws ChallengeNotFoundException if the challenge does not exist
     */
    @Override
    public void leaveChallenge(UserId userId, ChallengeUlid challengeUlid) {
        if (!forAccessingChallenges.existsByUlid(challengeUlid)) {
            throw new ChallengeNotFoundException(challengeUlid.getValue());
        }

        if (!forAccessingChallengeParticipations.existsByChallengeAndUser(userId, challengeUlid)) {
            return;
        }

        forAccessingChallengeParticipations.leaveChallenge(userId, challengeUlid);
        eventPublisher.publishEvent(new ChallengeLeftEvent(userId, challengeUlid));
        trophyManagementService.deleteTrophyIfExists(userId, challengeUlid);
    }
}
