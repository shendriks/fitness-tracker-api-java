package dev.shendriks.fitnesstrackerapi.activity;

public record ActivityRequest(String username, String activity, int duration, int calories) {
}
