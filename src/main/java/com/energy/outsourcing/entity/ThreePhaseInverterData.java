package com.energy.outsourcing.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("THREE")
@Getter @Setter
public class ThreePhaseInverterData extends InverterData {

    // 선간 전압
    private Double gridVoltageRS;
    private Double gridVoltageST;
    private Double gridVoltageTR;

    // 상 전류
    private Double gridCurrentR;
    private Double gridCurrentS;
    private Double gridCurrentT;

    private Double currentOutput; // 4byte
    private Double powerFactor;
}
