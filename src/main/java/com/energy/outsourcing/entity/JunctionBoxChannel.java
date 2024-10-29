package com.energy.outsourcing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class JunctionBoxChannel extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer channelNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "junction_box_id")
    private JunctionBox junctionBox;

    @OneToMany(mappedBy = "junctionBoxChannel", cascade = CascadeType.ALL)
    private List<JunctionBoxChannelData> channelDataList = new ArrayList<>();
}
