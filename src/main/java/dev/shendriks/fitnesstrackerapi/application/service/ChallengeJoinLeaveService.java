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

@Service
@AllArgsConstructor
@Transactional
public class ChallengeJoinLeaveService implements LeaveChallengeUseCase, JoinChallengeUseCase {
    private final ApplicationEventPublisher eventPublisher;
    private final ForAccessingChallenges forAccessingChallenges;
    private final ForAccessingChallengeParticipations forAccessingChallengeParticipations;
    private final ChallengeCompletionUpdateService challengeCompletionUpdateService;
    private final TrophyManagementService trophyManagementService;

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
