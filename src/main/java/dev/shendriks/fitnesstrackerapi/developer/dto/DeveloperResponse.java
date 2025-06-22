package dev.shendriks.fitnesstrackerapi.developer.dto;

import dev.shendriks.fitnesstrackerapi.application.dto.ApplicationResponse;

import java.util.List;

public record DeveloperResponse(Long id, String email, List<ApplicationResponse> applications) {
}
