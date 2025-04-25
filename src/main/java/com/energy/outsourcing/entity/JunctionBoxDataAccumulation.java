package com.energy.outsourcing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "junction_box_data_timestamp", columnList = "timestamp")
})
public class JunctionBoxDataAccumulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double cumulativeEnergy; // 0.1[W] 단위

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "junction_box_id")
    private JunctionBox junctionBox;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccumulationType type;

    public JunctionBoxDataAccumulation(Double cumulativeEnergy, JunctionBox junctionBox, LocalDateTime timestamp, AccumulationType type) {
        this.cumulativeEnergy = cumulativeEnergy;
        this.junctionBox = junctionBox;
        this.timestamp = timestamp;
        this.type = type;
    }
}
