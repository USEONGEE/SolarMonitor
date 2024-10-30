package com.energy.outsourcing.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 접속함 최신 데이터 응답 DTO
 */
@Data
@Schema(description = "접속함의 최신 전압 및 전류 데이터 응답 DTO")
public class JunctionBoxLatestDataResponseDto {

    @Schema(description = "접속함 ID", example = "1001")
    private Long junctionBoxId;

    @Schema(description = "합산된 전압 [V]", example = "220.5")
    private Double totalVoltage;

    @Schema(description = "합산된 전류 [A]", example = "15.3")
    private Double totalCurrent;

    public JunctionBoxLatestDataResponseDto(Long junctionBoxId, Double totalVoltage, Double totalCurrent) {
        this.junctionBoxId = junctionBoxId;
        this.totalVoltage = totalVoltage;
        this.totalCurrent = totalCurrent;
    }
}
