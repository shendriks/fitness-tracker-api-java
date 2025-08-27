package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table(name = "milestone")
public class MilestoneDbEntity extends AchievementDbEntity {
}
