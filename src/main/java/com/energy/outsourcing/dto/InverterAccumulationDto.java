package com.energy.outsourcing.dto;

import com.energy.outsourcing.entity.AccumulationType;
import com.energy.outsourcing.entity.InverterAccumulation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "인버터 누적 데이터 전송 객체")
public class InverterAccumulationDto {

    @Schema(description = "인버터 ID", example = "123")
    private Long inverterId;

    @Schema(description = "인버터의 누적 에너지", example = "1500.0")
    private Double cumulativeEnergy;

    @Schema(description = "누적 데이터의 날짜와 시간", example = "2024-10-30T23:59:59")
    private LocalDateTime date;

    @Schema(description = "누적 유형 (DAILY, HOURLY, MONTHLY)", example = "DAILY")
    private AccumulationType type;

    /**
     * InverterAccumulation 엔티티를 InverterAccumulationDto로 변환하는 메서드
     *
     * @param entity 변환할 InverterAccumulation 엔티티
     * @return 엔티티 데이터를 가진 새로운 InverterAccumulationDto 객체
     */
    public static InverterAccumulationDto fromEntity(InverterAccumulation entity) {
        return new InverterAccumulationDto(
                entity.getInverter().getId(),
                entity.getCumulativeEnergy(),
                entity.getDate(),
                entity.getType()
        );
    }
}