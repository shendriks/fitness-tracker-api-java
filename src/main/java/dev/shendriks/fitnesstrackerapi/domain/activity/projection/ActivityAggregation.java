package dev.shendriks.fitnesstrackerapi.domain.activity.projection;

public interface ActivityAggregation {
    Long getCount();

    Long getTotalDistance();

    Long getTotalDuration();

    Long getMaxDistance();

    Long getMaxDuration();
}
