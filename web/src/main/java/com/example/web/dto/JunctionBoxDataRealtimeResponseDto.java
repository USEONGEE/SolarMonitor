package com.example.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JunctionBoxDataRealtimeResponseDto {
    public Long id;
    public Double pvPower;
    public Double cumulativeEnergy;
}
