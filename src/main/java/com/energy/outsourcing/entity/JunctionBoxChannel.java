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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "junction_box_id")
    private JunctionBox junctionBox;
}
