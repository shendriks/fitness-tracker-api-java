package dev.shendriks.fitnesstrackerapi.activity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ActivityRepository extends 
        CrudRepository<Activity, Long>, 
        PagingAndSortingRepository<Activity, Long> {
}
