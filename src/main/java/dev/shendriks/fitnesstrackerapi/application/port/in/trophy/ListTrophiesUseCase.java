package dev.shendriks.fitnesstrackerapi.application.port.in.trophy;

import dev.shendriks.fitnesstrackerapi.domain.entity.Trophy;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

import java.util.List;

public interface ListTrophiesUseCase {
    List<Trophy> getAllTrophiesByUser(UserId userId);
}
