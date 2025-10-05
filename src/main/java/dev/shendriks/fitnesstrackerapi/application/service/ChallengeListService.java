package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.port.in.challenge.ListChallengesUseCase;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingChallenges;
import dev.shendriks.fitnesstrackerapi.domain.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service for listing challenges visible to a user.
 */
@Service
@AllArgsConstructor
public class ChallengeListService implements ListChallengesUseCase {
    private final ForAccessingChallenges forAccessingChallenges;

    /**
     * Returns all current challenges for the given user.
     *
     * @param userId the user identifier
     * @return list of challenges
     */
    @Override
    public List<Challenge> getAllChallenges(UserId userId) {
        return forAccessingChallenges.findAllByUser(userId);
    }
}
