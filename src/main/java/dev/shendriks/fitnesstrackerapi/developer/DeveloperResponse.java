package dev.shendriks.fitnesstrackerapi.developer;

import dev.shendriks.fitnesstrackerapi.application.ApplicationResponse;

import java.util.List;

public record DeveloperResponse(Long id, String email, List<ApplicationResponse> applications) {
}
