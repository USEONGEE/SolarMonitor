package com.example.web.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
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

    @Column(nullable = false)
    private LocalDateTime date; // 날짜

    private Long generationTime; // 발전 시간(분)

    public InverterAccumulation(Inverter inverter, Double cumulativeEnergy, AccumulationType type, LocalDateTime date) {
        this.inverter = inverter;
        this.cumulativeEnergy = cumulativeEnergy;
        this.type = type;
        this.date = date;
        this.generationTime = null;
    }

    public InverterAccumulation(Inverter inverter, Double cumulativeEnergy, AccumulationType type, LocalDateTime date, Long generationTime) {
        this.inverter = inverter;
        this.cumulativeEnergy = cumulativeEnergy;
        this.type = type;
        this.date = date;
        this.generationTime = generationTime;
    }
}
