package com.energy.outsourcing.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class JunctionBoxChannelData {

    @Id
    @GeneratedValue
    private Long id;

    private Double voltage; // 0.1[V] 단위
    private Double current; // 0.01[A] 단위

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "junction_box_channel_id")
    private JunctionBoxChannel junctionBoxChannel;
}
