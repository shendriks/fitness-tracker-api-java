package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.converter.SpeedConverter;
import dev.shendriks.fitnesstrackerapi.domain.value.Speed;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "kilometer_speed")
public class KilometerSpeedDbEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Convert(converter = SpeedConverter.class)
    @Column(nullable = false)
    private Speed speed;
    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private ActivityDbEntity activity;
}
