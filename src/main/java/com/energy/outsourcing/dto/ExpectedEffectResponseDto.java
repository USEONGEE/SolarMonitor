package com.energy.outsourcing.dto;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpectedEffectResponseDto {

    public Long tree;
    public Long cumulativeTree;
    public double oil;
    public double cumulativeOil;
    public double co2;
    public double cumulativeCo2;
}
