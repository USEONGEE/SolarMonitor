package com.energy.outsourcing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 단상 인버터 데이터 전송 DTO
 */
@Data
@AllArgsConstructor
@Schema(description = "단상 인버터 데이터 DTO")
public class SinglePhaseInverterDto {

    @Schema(description = "태양광 전압 [V]", example = "320.0")
    private Double pvVoltage;

    @Schema(description = "태양광 전류 [A]", example = "5.5")
    private Double pvCurrent;

    @Schema(description = "태양광 출력 [W]", example = "1760.0")
    private Double pvPower;

    @Schema(description = "그리드 전압 [V]", example = "230.0")
    private Double gridVoltage;

    @Schema(description = "그리드 전류 [A]", example = "4.8")
    private Double gridCurrent;

    @Schema(description = "현재 출력 [W]", example = "1104.0")
    private Double currentOutput;

    @Schema(description = "역률", example = "0.95")
    private Double powerFactor;

    @Schema(description = "주파수 [Hz]", example = "50.0")
    private Double frequency;

    @Schema(description = "누적 에너지 [kWh]", example = "1500.0")
    private Double cumulativeEnergy;

    @Schema(description = "고장 상태 코드", example = "0")
    private Integer faultStatus;
}
