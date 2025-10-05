package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.converter.SpeedConverter;
import dev.shendriks.fitnesstrackerapi.domain.value.Speed;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "speed_at_time")
public class SpeedAtTimeDbEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Instant timestamp;
    @Convert(converter = SpeedConverter.class)
    @Column(nullable = false)
    private Speed speed;
    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private ActivityDbEntity activity;
}
