package com.energy.outsourcing.dto;

import com.energy.outsourcing.entity.JunctionBoxData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class JunctionBoxDetailResponseDto {

    private Long id;
    private Double pvVoltage;

    @Schema(description = "태양광 전류 (PV Current)", example = "15.0")
    private Double pvCurrent;

    @Schema(description = "태양광 출력 전력 (PV Power)", example = "4500.0")
    private Double pvPower;
    @Schema(description = "누적 발전량", example = "1500.0")
    private Double cumulativeEnergy;

    @Schema(description = "데이터의 타임스탬프", example = "2024-10-30T14:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "전체 누적 발전량 합계", example = "30000.0")
    private Double totalMonthlyCumulativeEnergy;

    public static JunctionBoxDetailResponseDto fromJunctionBoxData(JunctionBoxData data, Double dailyCumulativeEnergy, Double totalMonthlyCumulativeEnergy) {
        return new JunctionBoxDetailResponseDto(
                data.getJunctionBox().getId(),
                data.getPvVoltage(),
                data.getPvCurrent(),
                data.getPower(),
                dailyCumulativeEnergy,
                data.getTimestamp(),
                totalMonthlyCumulativeEnergy
        );
    }
}
