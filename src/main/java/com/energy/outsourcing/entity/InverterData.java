package com.energy.outsourcing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter @Setter
public abstract class InverterData extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inverter_id")
    private Inverter inverter;

    private LocalDateTime timestamp;

    // 공통 필드
    private Double pvVoltage; // PV 전압 (평균) - [V]
    private Double pvCurrent; // PV 전류 (합) - [A]
    private Double pvPower; // PV 출력 - [W]
    private Double frequency; // 주파수 0.1 - [Hz]
    private Double cumulativeEnergy; // 누적 발전량 - [Wh]
    private Integer faultStatus; // 고장 상태
    private Double currentOutput; // 현재 출력 - [W]
    private Double powerFactor; // 역률 0.1 [%]

}
