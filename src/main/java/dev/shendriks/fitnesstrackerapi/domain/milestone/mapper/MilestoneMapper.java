package dev.shendriks.fitnesstrackerapi.domain.milestone.mapper;

import dev.shendriks.fitnesstrackerapi.domain.milestone.dto.MilestoneResponse;
import dev.shendriks.fitnesstrackerapi.domain.milestone.projection.MilestoneProjection;
import dev.shendriks.fitnesstrackerapi.supportive.Base64FileEncoder;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log
@Component
public class MilestoneMapper {
    private final Base64FileEncoder base64FileEncoder;

    public MilestoneMapper(Base64FileEncoder base64FileEncoder) {
        this.base64FileEncoder = base64FileEncoder;
    }

    public List<MilestoneResponse> toResponses(Iterable<MilestoneProjection> milestones) {
        List<MilestoneResponse> milestoneResponses = new ArrayList<>();
        for (MilestoneProjection milestone : milestones) {
            milestoneResponses.add(toResponse(milestone));
        }

        return milestoneResponses;
    }

    public MilestoneResponse toResponse(MilestoneProjection milestone) {
        Optional<String> imageData = base64FileEncoder.encodeFile(milestone.getImageFilePath());

        return new MilestoneResponse(
            milestone.getUlid(),
            milestone.getName(),
            milestone.getDescription(),
            imageData,
            milestone.isCompleted()
        );
    }
}
