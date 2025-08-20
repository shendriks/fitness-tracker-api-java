package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection;

public interface ActivityAggregationDbProjection {
    long getCount();

    long getTotalDistance();

    long getTotalDuration();

    long getMaxDistance();

    long getMaxDuration();
}
