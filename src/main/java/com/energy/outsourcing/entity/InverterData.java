package com.energy.outsourcing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "phase_type")
@Getter @Setter
public abstract class InverterData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private InverterType inverterType ;

    // 공통 필드
    private Double pvVoltage;
    private Double pvCurrent;
    private Double pvPower;
    private Double frequency;
    private Double cumulativeEnergy;
    private Integer faultStatus;

}
