package com.energy.outsourcing.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Inverter {
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private InverterType inverterType;

    private String deviceId;

    @OneToMany(mappedBy = "inverter")
    private List<InverterData> inverterDataList = new ArrayList<>();
}
