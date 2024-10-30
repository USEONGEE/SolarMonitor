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
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
public class JunctionBoxDataAccumulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double power; // 0.1[W] 단위

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "junction_box_id")
    private JunctionBox junctionBox;
    @Column(nullable = false)
    private LocalDateTime timestamp;
    private AccumulationType type; // HOURLY only

}
