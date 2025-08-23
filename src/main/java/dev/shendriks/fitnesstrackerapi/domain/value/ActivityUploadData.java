package dev.shendriks.fitnesstrackerapi.domain.value;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record ActivityUploadData(
    ActivityType activityType,
    String title,
    String description,
    MultipartFile gpxFile
) {
}
