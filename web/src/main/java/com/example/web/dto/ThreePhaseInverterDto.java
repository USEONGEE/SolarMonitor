package com.example.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 삼상 인버터 데이터 전송 DTO
 */
@Data
@AllArgsConstructor
@Schema(description = "삼상 인버터 데이터 DTO")
public class ThreePhaseInverterDto {

    @Schema(description = "태양광 전압 (평균) [V]", example = "320.0")

    private Double pvVoltage;

    @Schema(description = "태양광 전류 (합) [A]", example = "15.5")
    private Double pvCurrent;

    @Schema(description = "태양광 출력 [W]", example = "4960.0")
    private Double pvPower;

    @Schema(description = "RS 선간 전압 [V]", example = "400.0")
    private Double gridVoltageRS;

    @Schema(description = "ST 선간 전압 [V]", example = "400.0")
    private Double gridVoltageST;

    @Schema(description = "TR 선간 전압 [V]", example = "400.0")
    private Double gridVoltageTR;

    @Schema(description = "R상 전류 [A]", example = "5.0")
    private Double gridCurrentR;

    @Schema(description = "S상 전류 [A]", example = "5.1")
    private Double gridCurrentS;

    @Schema(description = "T상 전류 [A]", example = "5.2")
    private Double gridCurrentT;

    @Schema(description = "현재 출력 [W]", example = "2200.0")
    private Double currentOutput;

    @Schema(description = "역률", example = "0.98")
    private Double powerFactor;

    @Schema(description = "주파수 [Hz]", example = "60.0")
    private Double frequency;

    @Schema(description = "누적 에너지 [kWh]", example = "2500.0")
    private Double cumulativeEnergy;

    @Schema(description = "고장 상태 코드", example = "1")
    private Integer faultStatus;
}
