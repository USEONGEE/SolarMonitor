package com.energy.outsourcing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(indexes = {
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter @Setter
public abstract class InverterData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inverter_id", nullable = false)
    private Inverter inverter;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // 공통 필드
    @Column(nullable = false)
    private Double pvVoltage; // PV 전압 (평균) - [V]

    @Column(nullable = false)
    private Double pvCurrent; // PV 전류 (합) - [A]

    @Column(nullable = false)
    private Double pvPower; // PV 출력 - [W]

    @Column(nullable = false)
    private Double frequency; // 주파수 0.1 - [Hz]

    @Column(nullable = false)
    private Double cumulativeEnergy; // 누적 발전량 - [Wh]

    @Column(nullable = false)
    private Integer faultStatus; // 고장 상태

    @Column(nullable = false)
    private Double currentOutput; // 현재 출력 - [W]

    @Column(nullable = false)
    private Double powerFactor; // 역률 0.1 [%]

    protected void validate() {
        if (this.pvVoltage < 0) {
            throw new IllegalArgumentException("pvVoltage must be greater than or equal to 0");
        }
        if (this.pvCurrent < 0) {
            throw new IllegalArgumentException("pvCurrent must be greater than or equal to 0");
        }
        if (this.pvPower < 0) {
            throw new IllegalArgumentException("pvPower must be greater than or equal to 0");
        }
        if (this.frequency < 0) {
            throw new IllegalArgumentException("frequency must be greater than or equal to 0");
        }
        if (this.cumulativeEnergy < 0) {
            throw new IllegalArgumentException("cumulativeEnergy must be greater than or equal to 0");
        }
        if (this.faultStatus < 0) {
            throw new IllegalArgumentException("faultStatus must be greater than or equal to 0");
        }
        if (this.currentOutput < 0) {
            throw new IllegalArgumentException("currentOutput must be greater than or equal to 0");
        }
        if (this.powerFactor < 0) {
            throw new IllegalArgumentException("powerFactor must be greater than or equal to 0");
        }
    }
}

