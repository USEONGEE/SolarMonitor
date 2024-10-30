package com.energy.outsourcing.dto;

import com.energy.outsourcing.entity.InverterData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "인버터 세부 응답 데이터 전송 객체")
public class InverterDetailResponseDto {

    @Schema(description = "인버터 ID", example = "123")
    private Long inverterId;

    @Schema(description = "태양광 전압 (PV Voltage)", example = "300.0")
    private Double pvVoltage;

    @Schema(description = "태양광 전류 (PV Current)", example = "15.0")
    private Double pvCurrent;

    @Schema(description = "태양광 출력 전력 (PV Power)", example = "4500.0")
    private Double pvPower;

    @Schema(description = "누적 발전량", example = "1500.0")
    private Double cumulativeEnergy;

    @Schema(description = "데이터의 타임스탬프", example = "2024-10-30T14:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "해당 월의 누적 발전량 합계", example = "30000.0")
    private Double totalMonthlyCumulativeEnergy;

    /**
     * InverterData 엔티티와 월 누적 발전량을 기반으로 InverterDetailResponseDto 생성
     *
     * @param data                   변환할 InverterData 엔티티
     * @param totalMonthlyCumulativeEnergy 월 누적 발전량
     * @return InverterDetailResponseDto 객체
     */
    public static InverterDetailResponseDto fromInverterData(InverterData data, Double totalMonthlyCumulativeEnergy) {
        return new InverterDetailResponseDto(
                data.getInverter().getId(),
                data.getPvVoltage(),
                data.getPvCurrent(),
                data.getPvPower(),
                data.getCumulativeEnergy(),
                data.getTimestamp(),
                totalMonthlyCumulativeEnergy
        );
    }
}
