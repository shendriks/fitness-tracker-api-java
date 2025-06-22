package dev.shendriks.fitnesstrackerapi.domain.activity.dto;

public record ActivityRequest(String username, String activity, int duration, int calories) {
}
