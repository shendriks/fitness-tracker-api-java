package dev.shendriks.fitnesstrackerapi.activity;

import dev.shendriks.fitnesstrackerapi.application.Application;
import dev.shendriks.fitnesstrackerapi.ratelimiting.RateLimiterService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/tracker")
public class ActivityController {
    private final ActivityService activityService;
    private final RateLimiterService rateLimiterService;

    public ActivityController(ActivityService activityService, RateLimiterService rateLimiterService) {
        this.activityService = activityService;
        this.rateLimiterService = rateLimiterService;
    }

    @PostMapping
    public ResponseEntity<Void> postActivity(
        @AuthenticationPrincipal Application application, 
        @RequestBody ActivityRequest request
    ) {
        if (!rateLimiterService.isRequestAllowed(application)) {
            return ResponseEntity.status(429).build();
        }

        ActivityResponse activityResponse = activityService.save(request, application);
        URI location = URI.create("/api/tracker");// + activityResponse.id());

        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<?> getActivities(@AuthenticationPrincipal Application application) {
        if (!rateLimiterService.isRequestAllowed(application)) {
            return ResponseEntity.status(429).build();
        }

        Iterable<ActivityResponse> activities = activityService.getAllActivities();

        return ResponseEntity.ok(activities);
    }
}
