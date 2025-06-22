package dev.shendriks.fitnesstrackerapi.domain.activity.repository;

import dev.shendriks.fitnesstrackerapi.domain.activity.entity.Activity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ActivityRepository extends 
        CrudRepository<Activity, Long>, 
        PagingAndSortingRepository<Activity, Long> {
}
