package dev.shendriks.fitnesstrackerapi.developer;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/developers")
public class DeveloperController {
    private final DeveloperService service;

    public DeveloperController(DeveloperService service) {
        this.service = service;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerDeveloper(@Valid @RequestBody DeveloperSignupRequest request) {
        DeveloperResponse developer = service.findDeveloperByEmail(request.email());
        if (developer != null) {
            return ResponseEntity.badRequest().build();
        }

        DeveloperResponse developerResponse = this.service.save(request);
        URI location = URI.create("/api/developers/" + developerResponse.id());
        
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeveloperResponse> getDeveloper(@AuthenticationPrincipal UserDetails details, @PathVariable String id) {
        DeveloperResponse developerResponse = this.service.findDeveloperById(id);
        if (developerResponse == null) {
            return ResponseEntity.notFound().build();
        }

        if (details == null || !details.getUsername().equals(developerResponse.email())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok().body(developerResponse);
    }
}
