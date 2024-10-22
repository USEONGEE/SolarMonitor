package com.energy.outsourcing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class JunctionBoxChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer channelNumber;
    private Double voltage; // 0.1[V] 단위
    private Double current; // 0.01[A] 단위

    @ManyToOne
    @JoinColumn(name = "junction_box_id")
    private JunctionBox junctionBox;
}
