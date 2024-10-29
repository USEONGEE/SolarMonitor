package com.energy.outsourcing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.energy.outsourcing.dto.ThreePhaseInverterDto;

@Getter
@Setter
@Entity
public class ThreePhaseInverterData extends InverterData {

    private Double pvVoltage;
    private Double pvCurrent;
    private Double pvPower;

    private Double gridVoltageRS;
    private Double gridVoltageST;
    private Double gridVoltageTR;

    private Double gridCurrentR;
    private Double gridCurrentS;
    private Double gridCurrentT;

    private Double currentOutput;
    private Double powerFactor;
    private Double frequency;
    private Double cumulativeEnergy;
    private Integer faultStatus;

    public static ThreePhaseInverterData fromDTO(ThreePhaseInverterDto dto) {
        ThreePhaseInverterData data = new ThreePhaseInverterData();
        data.setPvVoltage(dto.getPvVoltage());
        data.setPvCurrent(dto.getPvCurrent());
        data.setPvPower(dto.getPvPower());

        data.setGridVoltageRS(dto.getGridVoltageRS());
        data.setGridVoltageST(dto.getGridVoltageST());
        data.setGridVoltageTR(dto.getGridVoltageTR());

        data.setGridCurrentR(dto.getGridCurrentR());
        data.setGridCurrentS(dto.getGridCurrentS());
        data.setGridCurrentT(dto.getGridCurrentT());

        data.setCurrentOutput(dto.getCurrentOutput());
        data.setPowerFactor(dto.getPowerFactor());
        data.setFrequency(dto.getFrequency());
        data.setCumulativeEnergy(dto.getCumulativeEnergy());
        data.setFaultStatus(dto.getFaultStatus());

        return data;
    }
}
