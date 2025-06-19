package dev.shendriks.fitnesstrackerapi.error;

import java.time.Instant;

public record ErrorMessage(int statusCode, Instant timestamp, String message, String description) {
}