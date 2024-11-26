package com.energy.outsourcing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class JunctionBox {

    @Id
    @GeneratedValue(generator = "increment")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inverter_id")
    private Inverter inverter;

    private String deviceId;

    private Long module_count;

}
