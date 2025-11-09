package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.validation.enums.ValueOfEnum;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.validation.gpx.GPXFile;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record ActivityUploadRequestDTO(
    @NotBlank
    @ValueOfEnum(enumClass = ActivityType.class)
    @Schema(implementation = ActivityType.class, description = "The type of activity", example = "cycling")
    String activityType,
    @NotBlank
    @Schema(description = "Title of the activity", example = "Evening Ride", type = "string")
    String title,
    @Schema(description = "Optional description of the activity", example = "Chill ride along the river.", type = "string")
    String description,
    @NotNull
    @GPXFile
    @Schema(description = "GPX file containing the recorded track", type = "string", format = "binary")
    MultipartFile gpxFile
) {
}
