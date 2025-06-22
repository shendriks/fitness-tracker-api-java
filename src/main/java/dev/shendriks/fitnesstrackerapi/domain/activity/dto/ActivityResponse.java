package dev.shendriks.fitnesstrackerapi.domain.activity.dto;

public record ActivityResponse(
    long id,
    String username,
    String activity,
    int duration,
    int calories,
    String application
) {
}
