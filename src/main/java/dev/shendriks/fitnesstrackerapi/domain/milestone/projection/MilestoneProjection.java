package dev.shendriks.fitnesstrackerapi.domain.milestone.projection;

public interface MilestoneProjection {
    String getUlid();

    String getName();

    String getDescription();

    String getImageFilePath();

    boolean isCompleted();
}
