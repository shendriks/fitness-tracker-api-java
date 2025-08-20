package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "milestone")
public class MilestoneDbEntity extends AchievementDbEntity {
}
