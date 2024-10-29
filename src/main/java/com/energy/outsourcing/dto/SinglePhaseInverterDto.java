package com.energy.outsourcing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SinglePhaseInverterDto {
    private Double pvVoltage;
    private Double pvCurrent;
    private Double pvPower;
    private Double gridVoltage;
    private Double gridCurrent;
    private Double currentOutput;
    private Double powerFactor;
    private Double frequency;
    private Double cumulativeEnergy;
    private Integer faultStatus;
}