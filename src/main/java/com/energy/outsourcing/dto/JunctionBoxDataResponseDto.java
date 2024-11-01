package com.energy.outsourcing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class JunctionBoxDataResponseDto {

    public Long junctionBoxId;
    private Double pvPower;
    private LocalDateTime timestamp;
}
