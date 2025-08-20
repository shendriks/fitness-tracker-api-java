package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.port.in.challenge.ListChallengesUseCase;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingChallenges;
import dev.shendriks.fitnesstrackerapi.domain.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ChallengeListService implements ListChallengesUseCase {
    private final ForAccessingChallenges forAccessingChallenges;

    @Override
    public List<Challenge> getAllChallenges(UserId userId) {
        return forAccessingChallenges.findAllByUser(userId);
    }
}
