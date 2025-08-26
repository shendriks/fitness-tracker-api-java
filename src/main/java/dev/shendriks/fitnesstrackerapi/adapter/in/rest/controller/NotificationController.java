package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.ApiErrorResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.NotificationResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper.NotificationDTOMapper;
import dev.shendriks.fitnesstrackerapi.application.port.in.notification.ListAllNotificationsUseCase;
import dev.shendriks.fitnesstrackerapi.application.port.in.notification.ListNewNotificationsUseCase;
import dev.shendriks.fitnesstrackerapi.domain.entity.Notification;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.value.NotificationUlid;
import dev.shendriks.fitnesstrackerapi.infrastructure.openapi.OpenApiTagName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static dev.shendriks.fitnesstrackerapi.infrastructure.security.SecurityRequirementName.BEARER_TOKEN;

@RestController
@Tag(name = OpenApiTagName.NOTIFICATIONS, description = "Notifications")
@RequestMapping("/api/notifications")
@SecurityRequirement(name = BEARER_TOKEN)
@AllArgsConstructor
public class NotificationController {
    private final ListAllNotificationsUseCase listAllNotificationsUseCase;
    private final ListNewNotificationsUseCase listNewNotificationsUseCase;
    private final NotificationDTOMapper mapper;

    @Operation(summary = "Get all notifications")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "All notifications",
            content = {
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = NotificationResponseDTO.class)))
            }
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
    })
    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getNotifications(
        @AuthenticationPrincipal(errorOnInvalidType = true) User user,
        @RequestParam(required = false) String sinceId
    ) {
        if (sinceId == null) {
            List<Notification> notifications = listAllNotificationsUseCase.findAllByUser(user.id());
            return ResponseEntity.ok(mapper.toNotificationResponseDTOs(notifications));
        }

        List<Notification> notifications = listNewNotificationsUseCase.findAllByUserSince(
            user.id(),
            new NotificationUlid(sinceId)
        );
        return ResponseEntity.ok(mapper.toNotificationResponseDTOs(notifications));
    }
}
