package dev.shendriks.fitnesstrackerapi.domain.achievement.controller;

import dev.shendriks.fitnesstrackerapi.domain.achievement.dto.AchievementResponse;
import dev.shendriks.fitnesstrackerapi.domain.achievement.service.AchievementService;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
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
import org.springframework.web.bind.annotation.RestController;

import static dev.shendriks.fitnesstrackerapi.security.SecurityRequirementName.BEARER_TOKEN;

@RestController
@Tag(name = OpenApiTagName.ACHIEVEMENTS, description = "A user's achievements")
@RequestMapping("/api/achievements")
@SecurityRequirement(name = BEARER_TOKEN)
public class AchievementController {
    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @Operation(summary = "Get user's achievements")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The user's achievements",
            content = {
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = AchievementResponse.class)))
            }
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    })
    @GetMapping
    public ResponseEntity<Iterable<AchievementResponse>> getAchievements(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(achievementService.getAllAchievementsByUser(user));
    }
}
