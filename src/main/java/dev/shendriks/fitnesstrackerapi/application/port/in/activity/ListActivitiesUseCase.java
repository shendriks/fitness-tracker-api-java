package dev.shendriks.fitnesstrackerapi.application.port.in.activity;

import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

import java.util.List;

public interface ListActivitiesUseCase {
    List<Activity> getAllActivitiesByUser(UserId userId);
}
