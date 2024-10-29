package com.energy.outsourcing.dto;

import lombok.Data;

/**
 * 접속함 최신 데이터 응답 DTO
 */
@Data
public class JunctionBoxLatestDataResponseDto {

    private Long junctionBoxId;
    private Double totalVoltage; // 합산된 전압 - [V]
    private Double totalCurrent; // 합산된 전류 - [A]

    public JunctionBoxLatestDataResponseDto(Long junctionBoxId, Double totalVoltage, Double totalCurrent) {
        this.junctionBoxId = junctionBoxId;
        this.totalVoltage = totalVoltage;
        this.totalCurrent = totalCurrent;
    }
}