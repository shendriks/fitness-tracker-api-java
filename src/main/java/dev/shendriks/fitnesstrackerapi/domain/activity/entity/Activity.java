package dev.shendriks.fitnesstrackerapi.domain.activity.entity;

import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.application.entity.Application;
import dev.shendriks.fitnesstrackerapi.supportive.ulid.Ulid;
import jakarta.persistence.*;

@Entity
public class Activity {
    @Id
    @GeneratedValue
    private long id;
    @Ulid
    @Column(nullable = false, unique = true, length = 26)
    private String ulid;
    @Column(nullable = false)
    // note: user != developer!
    private String username;
    @Column(nullable = false)
    private ActivityType activityType;
    @Column(nullable = false)
    private int duration;
    @Column(nullable = false)
    private int calories;
    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getUlid() {
        return ulid;
    }

    public void setUlid(String ulid) {
        this.ulid = ulid;
    }
}
