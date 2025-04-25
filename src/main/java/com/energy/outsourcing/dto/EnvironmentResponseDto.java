package com.energy.outsourcing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EnvironmentResponseDto {
    public float moduleSurfaceTemperature;
    public float externalTemperature;
    public float horizontalInsolation;
    public float verticalInsolation;
    public Unit unit = new Unit();

    public static EnvironmentResponseDto of(float moduleSurfaceTemperature, float externalTemperature, float horizontalInsolation, float verticalInsolation) {
        EnvironmentResponseDto dto = new EnvironmentResponseDto();
        dto.moduleSurfaceTemperature = moduleSurfaceTemperature;
        dto.externalTemperature = externalTemperature;
        dto.horizontalInsolation = horizontalInsolation;
        dto.verticalInsolation = verticalInsolation;
        return dto;
    }

    public static class Unit {
        public String moduleSurfaceTemperature = "°C";
        public String externalTemperature = "°C";
        public String horizontalInsolation = "W/m²";
        public String verticalInsolation = "W/m²";

    }
}
