package dev.shendriks.fitnesstrackerapi.activity;

import dev.shendriks.fitnesstrackerapi.application.Application;
import dev.shendriks.fitnesstrackerapi.application.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/tracker")
public class ActivityController {
    private static final int MAX_TOKENS = 1;
    private final ConcurrentHashMap<String, AtomicInteger> tokenBuckets = new ConcurrentHashMap<>();
    private final ActivityService activityService;
    
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping
    public ResponseEntity<Void> postActivity(
        @AuthenticationPrincipal Application application, 
        @RequestBody ActivityRequest request
    ) {
        ResponseEntity<Void> response = checkRateLimit(application);
        if (response != null) return response;

        ActivityResponse activityResponse = activityService.save(request, application);
        URI location = URI.create("/api/tracker");// + activityResponse.id());

        return ResponseEntity.created(location).build();
    }
    
    @GetMapping
    public ResponseEntity<?> getActivities(@AuthenticationPrincipal Application application) {
        ResponseEntity<Void> response = checkRateLimit(application);
        if (response != null) return response;
        
        Iterable<ActivityResponse> activities = activityService.getAllActivities();
        
        return ResponseEntity.ok(activities);
    }
    
    @Scheduled(fixedRate = 1000)
    public void resetTokenBucket() {
        synchronized (tokenBuckets) {
            System.out.println("Resetting token buckets");
            tokenBuckets.values().forEach(counter -> counter.set(MAX_TOKENS));
        }
    }

    private ResponseEntity<Void> checkRateLimit(Application application) {
        if (Category.BASIC.equals(application.getCategory())) {
            var apiKey = application.getApiKey();

            synchronized (tokenBuckets) {
                tokenBuckets.putIfAbsent(apiKey, new AtomicInteger(MAX_TOKENS));
                if (tokenBuckets.get(apiKey).get() <= 0) {
                    return ResponseEntity.status(429).build();
                }
                tokenBuckets.get(apiKey).decrementAndGet();
            }
        }
        return null;
    }
}
