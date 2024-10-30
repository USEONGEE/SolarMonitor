package com.energy.outsourcing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "인버터 다수 조회용 응답 DTO")
public class InvertersDataResponseDto {

    @Schema(description = "인버터 ID", example = "123")
    private Long inverterId;

    @Schema(description = "실시간 전력량 (kW)", example = "3.5")
    private Double realtimeKw;

    @Schema(description = "일일 누적 발전량 (kW)", example = "45.6")
    private Double dailyCumulativeKw;

    public InvertersDataResponseDto(Long inverterId, Double realtimeKw, Double dailyCumulativeKw) {
        this.inverterId = inverterId;
        this.realtimeKw = realtimeKw;
        this.dailyCumulativeKw = dailyCumulativeKw;
    }
}
