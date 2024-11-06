package com.energy.outsourcing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentResponseDto {
    public float moduleSurfaceTemperature;
    public float externalTemperature;
    public float horizontalInsolation;
    public float verticalInsolation;

}
