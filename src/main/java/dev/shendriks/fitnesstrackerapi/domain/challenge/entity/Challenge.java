package dev.shendriks.fitnesstrackerapi.domain.challenge.entity;

import dev.shendriks.fitnesstrackerapi.domain.achievement.entity.Achievement;
import dev.shendriks.fitnesstrackerapi.supportive.ulid.Ulid;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Ulid
    @Column(nullable = false, unique = true, length = 26)
    private String ulid;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, length = 1023)
    private String description;
    @Column(nullable = false)
    private String ruleJson;
    @OneToMany(mappedBy = "challenge")
    private Set<Achievement> achievements;
}
