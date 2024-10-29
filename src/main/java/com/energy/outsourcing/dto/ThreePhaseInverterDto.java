package com.energy.outsourcing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ThreePhaseInverterDto {
    private Double pvVoltage; // 평균
    private Double pvCurrent; // 합
    private Double pvPower;

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
    private Double frequency;
    private Double cumulativeEnergy;
    private Integer faultStatus;
}