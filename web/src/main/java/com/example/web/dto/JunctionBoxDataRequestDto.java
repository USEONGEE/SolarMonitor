package com.example.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JunctionBoxDataRequestDto {
    public double pvVoltage;
    public double pvCurrent;
}
