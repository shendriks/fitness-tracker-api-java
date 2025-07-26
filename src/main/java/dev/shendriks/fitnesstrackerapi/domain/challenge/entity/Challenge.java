package dev.shendriks.fitnesstrackerapi.domain.challenge.entity;

import dev.shendriks.fitnesstrackerapi.domain.achievement.entity.Achievement;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Entity
@Getter
@Setter
public class Challenge extends Achievement {
    @Column(nullable = false)
    private Instant startDate;
    @Column(nullable = false)
    private Instant endDate;
    @OneToMany(mappedBy = "challenge", fetch = FetchType.EAGER)
    private Set<ChallengeParticipation> challengeParticipations;
}
