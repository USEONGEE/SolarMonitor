package com.energy.outsourcing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 접속반 데이터
 * 체널별 데이터를 총합으로 계산하여 저장
 */
@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
public class JunctionBoxData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double voltage; // 0.1[V] 단위
    private Double current; // 0.01[A] 단위
    private Double power; // 0.1[W] 단위

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "junction_box_id")
    private JunctionBox junctionBox;
    @Column(nullable = false)
    private LocalDateTime timestamp;

    public JunctionBoxData(Double voltage, Double current, Double power, JunctionBox junctionBox, LocalDateTime timestamp) {
        this.voltage = voltage;
        this.current = current;
        this.power = power;
        this.junctionBox = junctionBox;
        this.timestamp = timestamp;

    }
}
