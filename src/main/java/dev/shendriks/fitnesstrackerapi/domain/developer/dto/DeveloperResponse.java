package dev.shendriks.fitnesstrackerapi.domain.developer.dto;

import dev.shendriks.fitnesstrackerapi.domain.application.dto.ApplicationResponse;

import java.util.List;

public record DeveloperResponse(
    Long id, 
    String email, 
    List<ApplicationResponse> applications
) {
}
