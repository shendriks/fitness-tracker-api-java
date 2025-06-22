package dev.shendriks.fitnesstrackerapi.activity.dto;

public record ActivityRequest(String username, String activity, int duration, int calories) {
}
