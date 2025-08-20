package dev.shendriks.fitnesstrackerapi.application.port.in.challenge;

import dev.shendriks.fitnesstrackerapi.domain.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

import java.util.List;

public interface ListChallengesUseCase {
    List<Challenge> getAllChallenges(UserId userId);
}
