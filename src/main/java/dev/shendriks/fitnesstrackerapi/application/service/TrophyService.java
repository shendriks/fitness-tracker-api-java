package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.port.in.trophy.ListTrophiesUseCase;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingTrophies;
import dev.shendriks.fitnesstrackerapi.domain.entity.Trophy;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service exposing trophy queries.
 */
@Service
@AllArgsConstructor
public class TrophyService implements ListTrophiesUseCase {
    private final ForAccessingTrophies forAccessingTrophies;

    /**
     * Lists all trophies owned by the given user.
     *
     * @param userId the user identifier
     * @return list of trophies
     */
    @Override
    public List<Trophy> getAllTrophiesByUser(UserId userId) {
        return forAccessingTrophies.findAllByUser(userId);
    }
}
