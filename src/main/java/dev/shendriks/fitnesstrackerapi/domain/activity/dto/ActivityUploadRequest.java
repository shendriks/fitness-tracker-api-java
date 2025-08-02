package dev.shendriks.fitnesstrackerapi.domain.activity.dto;

import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.validation.enums.ValueOfEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ActivityUploadRequest(
    @NotBlank
    @ValueOfEnum(enumClass = ActivityType.class)
    @Schema(implementation = ActivityType.class)
    String activityType,
    @NotBlank
    String title,
    String description,
    @NotNull
    MultipartFile file
) {
}
