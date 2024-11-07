package com.energy.outsourcing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.energy.outsourcing.dto.ThreePhaseInverterDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class ThreePhaseInverterData extends InverterData {


    private Double gridVoltageRS;
    private Double gridVoltageST;
    private Double gridVoltageTR;

    private Double gridCurrentR;
    private Double gridCurrentS;
    private Double gridCurrentT;

    public static ThreePhaseInverterData fromDTO(ThreePhaseInverterDto dto, LocalDateTime timestamp) {
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
        data.setTimestamp(timestamp);
        return data;
    }
}
