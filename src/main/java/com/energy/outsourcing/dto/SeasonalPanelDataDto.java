package com.energy.outsourcing.dto;

import com.energy.outsourcing.entity.SeasonalPanelData;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeasonalPanelDataDto {
    private Double inclinedIrradiance;
    private Double ambientTemperature;
    private Double horizontalIrradiance;
    private Double panelTemperature;

    public SeasonalPanelDataDto(SeasonalPanelData seasonalPanelData) {
        this.inclinedIrradiance = seasonalPanelData.getInclinedIrradiance();
        this.ambientTemperature = seasonalPanelData.getAmbientTemperature();
        this.horizontalIrradiance = seasonalPanelData.getHorizontalIrradiance();
        this.panelTemperature = seasonalPanelData.getPanelTemperature();
    }
}
