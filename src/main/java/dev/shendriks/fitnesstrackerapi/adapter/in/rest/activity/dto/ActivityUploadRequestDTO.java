package dev.shendriks.fitnesstrackerapi.adapter.in.rest.activity.dto;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.validation.enums.ValueOfEnum;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.validator.GPXFile;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ActivityUploadRequestDTO(
    @NotBlank
    @ValueOfEnum(enumClass = ActivityType.class)
    @Schema(implementation = ActivityType.class)
    String activityType,
    @NotBlank
    String title,
    String description,
    @NotNull
    @GPXFile
    MultipartFile gpxFile
) {
}
