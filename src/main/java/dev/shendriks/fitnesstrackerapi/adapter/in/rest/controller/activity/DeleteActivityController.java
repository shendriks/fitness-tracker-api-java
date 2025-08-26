package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.activity;

import dev.shendriks.fitnesstrackerapi.application.port.in.activity.DeleteActivityUseCase;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityUlid;
import dev.shendriks.fitnesstrackerapi.infrastructure.openapi.OpenApiTagName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static dev.shendriks.fitnesstrackerapi.infrastructure.security.SecurityRequirementName.BEARER_TOKEN;

@RestController
@Tag(name = OpenApiTagName.ACTIVITIES)
@SecurityRequirement(name = BEARER_TOKEN)
@AllArgsConstructor
public class DeleteActivityController {
    private final DeleteActivityUseCase deleteActivityUseCase;

    @Operation(summary = "Delete activity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Activity deleted", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded", content = @Content)})
    @DeleteMapping("/api/activities/{id}")
    public ResponseEntity<Void> deleteActivity(
        @AuthenticationPrincipal(errorOnInvalidType = true)
        User user,
        @PathVariable
        @Parameter(
            name = "id",
            description = "The activity id",
            required = true,
            examples = @ExampleObject(value = "01JZQZZYETYBWQV7KF0BHGMBDJ")
        )
        String id
    ) {
        deleteActivityUseCase.deleteActivityForUser(user.id(), new ActivityUlid(id));
        return ResponseEntity.noContent().build();
    }
}
