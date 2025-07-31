package dev.shendriks.fitnesstrackerapi.domain.notification.controller;

import dev.shendriks.fitnesstrackerapi.domain.notification.dto.NotificationResponse;
import dev.shendriks.fitnesstrackerapi.domain.notification.service.NotificationService;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import dev.shendriks.fitnesstrackerapi.error.ApiError;
import dev.shendriks.fitnesstrackerapi.openapi.OpenApiTagName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static dev.shendriks.fitnesstrackerapi.security.SecurityRequirementName.BEARER_TOKEN;

@RestController
@Tag(name = OpenApiTagName.NOTIFICATIONS, description = "Notifications")
@RequestMapping("/api/notifications")
@SecurityRequirement(name = BEARER_TOKEN)
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Get all notifications")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "All notifications",
            content = {
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = NotificationResponse.class)))
            }
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    @GetMapping
    public ResponseEntity<Iterable<NotificationResponse>> getMilestones(
        @AuthenticationPrincipal User user,
        @RequestParam(required = false) String sinceId
    ) {
        if (sinceId == null) {
            return ResponseEntity.ok(notificationService.findAllByUser(user));
        }
        return ResponseEntity.ok(notificationService.findAllByUserSince(user, sinceId));
    }
}
