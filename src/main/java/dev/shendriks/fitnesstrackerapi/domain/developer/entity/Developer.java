package dev.shendriks.fitnesstrackerapi.domain.developer.entity;

import dev.shendriks.fitnesstrackerapi.domain.application.entity.Application;
import dev.shendriks.fitnesstrackerapi.supportive.ulid.Ulid;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    public void addApplication(Application application) {
        this.applications.add(application);
    }
}
