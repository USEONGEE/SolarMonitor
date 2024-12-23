package com.energy.outsourcing.dto;

import com.energy.outsourcing.entity.SeasonalPanelData;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeasonalPanelDataDto {
    private Double verticalInsolation;
    private Double externalTemperature;
    private Double horizontalInsolation;
    private Double moduleSurfaceTemperature;
    public EnvironmentResponseDto.Unit unit = new EnvironmentResponseDto.Unit();

    public SeasonalPanelDataDto(SeasonalPanelData seasonalPanelData) {
        this.verticalInsolation = seasonalPanelData.getInclinedIrradiance();
        this.externalTemperature = seasonalPanelData.getAmbientTemperature();
        this.horizontalInsolation = seasonalPanelData.getHorizontalIrradiance();
        this.moduleSurfaceTemperature = seasonalPanelData.getPanelTemperature();
    }

    public static class Unit {
        public String moduleSurfaceTemperature = "°C";
        public String externalTemperature = "°C";
        public String horizontalInsolation = "W/m²";
        public String verticalInsolation = "W/m²";

    }
}
