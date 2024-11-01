package com.energy.outsourcing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JunctionBoxDataRealtimeResponseDto {

    public Long id;
    public Double pvPower;
    public Double cumulativeEnergy;
}
