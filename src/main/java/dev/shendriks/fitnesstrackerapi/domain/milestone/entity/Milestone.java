package dev.shendriks.fitnesstrackerapi.domain.milestone.entity;

import dev.shendriks.fitnesstrackerapi.domain.achievement.entity.Achievement;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Milestone extends Achievement {
}
