package com.example.web.entity;

import com.example.web.dto.SinglePhaseInverterDto;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("SINGLE")
@Getter @Setter
public class SinglePhaseInverterData extends InverterData {
    private Double gridVoltage; // 인버터 출력 전압
    private Double gridCurrent;  // 인버터 출력 전류

    public static SinglePhaseInverterData fromDTO(SinglePhaseInverterDto dto) {
        SinglePhaseInverterData data = new SinglePhaseInverterData();
        data.setPvVoltage(dto.getPvVoltage());
        data.setPvCurrent(dto.getPvCurrent());
        data.setPvPower(dto.getPvPower());
        data.setGridVoltage(dto.getGridVoltage());
        data.setGridCurrent(dto.getGridCurrent());
        data.setCurrentOutput(dto.getCurrentOutput());
        data.setPowerFactor(dto.getPowerFactor());
        data.setFrequency(dto.getFrequency());
        data.setCumulativeEnergy(dto.getCumulativeEnergy());
        data.setFaultStatus(dto.getFaultStatus());
        data.setTimestamp(LocalDateTime.now());
        data.validate();
        return data;
    }

    protected void validate() {
        super.validate();
        if (this.gridVoltage < 0) {
            throw new IllegalArgumentException("gridVoltage must be greater than or equal to 0");
        }
        if (this.gridCurrent < 0) {
            throw new IllegalArgumentException("gridCurrent must be greater than or equal to 0");
        }
    }
}
