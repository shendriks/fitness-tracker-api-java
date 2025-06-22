package dev.shendriks.fitnesstrackerapi.activity;

import dev.shendriks.fitnesstrackerapi.application.Application;
import jakarta.persistence.*;

@Entity
public class Activity {
    @Id
    @GeneratedValue
    private long id;
    @Column(nullable = false)
    // note: user != developer!
    private String username;
    @Column(nullable = false)
    private String activity;
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

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
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
}
