package dev.shendriks.fitnesstrackerapi.domain.activity.projection;

public interface ActivityAggregation {
    long getCount();

    long getTotalDistance();

    long getTotalDuration();

    long getMaxDistance();

    long getMaxDuration();
}
