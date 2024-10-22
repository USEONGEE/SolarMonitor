package com.energy.outsourcing.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("SINGLE")
@Getter @Setter
public class SinglePhaseInverterData extends InverterData {

    private Double gridVoltage;
    private Double gridCurrent;
    private Double currentOutput;
    private Double powerFactor;
}
