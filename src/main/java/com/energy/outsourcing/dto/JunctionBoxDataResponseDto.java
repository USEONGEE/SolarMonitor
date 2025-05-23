package com.energy.outsourcing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JunctionBoxDataResponseDto {
    public Long junctionBoxId;
    private Double pvPower;
    private LocalDateTime timestamp;
}
