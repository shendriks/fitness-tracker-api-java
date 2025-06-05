package dev.shendriks.fitnesstrackerapi.application;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/applications")
public class ApplicationController {
    private final ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<ApplicationRegisterResponse> registerApplication(
            @AuthenticationPrincipal UserDetails details,
            @RequestBody @Valid ApplicationRegisterRequest request
    ) {
        try {
            var response = service.register(request, details);
            return ResponseEntity.created(null).body(response);
        } catch (ApplicationAlreadyExistsException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
