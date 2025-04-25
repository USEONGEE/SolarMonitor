package com.energy.outsourcing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JunctionBoxDataRequestDto {
    public double pvVoltage;
    public double pvCurrent;
}
