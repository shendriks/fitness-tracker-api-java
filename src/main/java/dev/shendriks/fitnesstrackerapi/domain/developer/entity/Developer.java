package dev.shendriks.fitnesstrackerapi.domain.developer.entity;

import dev.shendriks.fitnesstrackerapi.domain.application.entity.Application;
import dev.shendriks.fitnesstrackerapi.supportive.ulid.Ulid;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Developer {
    @Id
    @GeneratedValue
    private Long id;
    @Ulid
    @Column(nullable = false, unique = true, length = 26)
    private String ulid;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String authority;
    @OneToMany(mappedBy = "developer")
    private List<Application> applications = new ArrayList<>();
    
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }
    
    public void addApplication(Application application) {
        this.applications.add(application);
    }

    public String getUlid() {
        return ulid;
    }

    public void setUlid(String ulid) {
        this.ulid = ulid;
    }
}
