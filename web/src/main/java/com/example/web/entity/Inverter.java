package com.example.web.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Inverter extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private InverterType inverterType;

    private String deviceId;

    @OneToMany(mappedBy = "inverter")
    private List<InverterData> inverterDataList = new ArrayList<>();

    @OneToMany(mappedBy = "inverter")
    private List<JunctionBox> junctionBoxes = new ArrayList<>();
}
