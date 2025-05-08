package com.example.web.dto;

import com.example.web.entity.SeasonalPanelData;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class SeasonalPanelDataDto {
    private Double verticalInsolation;
    private Double externalTemperature;
    private Double horizontalInsolation; // 수평 일사량, 주로 사용됨
    private Double moduleSurfaceTemperature;
    private Long inverterId;
    public EnvironmentResponseDto.Unit unit = new EnvironmentResponseDto.Unit();

    public SeasonalPanelDataDto(SeasonalPanelData seasonalPanelData) {
        this.verticalInsolation = seasonalPanelData.getInclinedIrradiance();
        this.externalTemperature = seasonalPanelData.getAmbientTemperature();
        this.horizontalInsolation = seasonalPanelData.getHorizontalIrradiance();
        this.moduleSurfaceTemperature = seasonalPanelData.getPanelTemperature();
        this.inverterId = seasonalPanelData.getInverterId();
    }

    public SeasonalPanelDataDto(Double verticalInsolation, Double externalTemperature,
                                Double horizontalInsolation, Double moduleSurfaceTemperature, Long inverterId) {
        this.verticalInsolation = verticalInsolation;
        this.externalTemperature = externalTemperature;
        this.horizontalInsolation = horizontalInsolation;
        this.moduleSurfaceTemperature = moduleSurfaceTemperature;
        this.inverterId = inverterId;
    }

    public static class Unit {
        public String moduleSurfaceTemperature = "°C";
        public String externalTemperature = "°C";
        public String horizontalInsolation = "W/m²";
        public String verticalInsolation = "W/m²";

    }
}
