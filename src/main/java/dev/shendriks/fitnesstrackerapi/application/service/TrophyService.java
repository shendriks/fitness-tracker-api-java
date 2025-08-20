package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.port.in.trophy.ListTrophiesUseCase;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingTrophies;
import dev.shendriks.fitnesstrackerapi.domain.entity.Trophy;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TrophyService implements ListTrophiesUseCase {
    private final ForAccessingTrophies forAccessingTrophies;

    @Override
    public List<Trophy> getAllTrophiesByUser(UserId userId) {
        return forAccessingTrophies.findAllByUser(userId);
    }
}
