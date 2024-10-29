package com.energy.outsourcing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class InverterAccumulation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inverter_id")
    private Inverter inverter;

    private Double cumulativeEnergy; // 누적 발전량
    @Enumerated(EnumType.STRING)
    private AccumulationType type; // DAILY or MONTHLY

    private LocalDateTime date; // 날짜
}
