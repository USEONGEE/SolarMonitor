package com.example.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Schema(description = "인버터 다수 조회용 응답 DTO")
@NoArgsConstructor
public class InvertersDataResponseDto {

    @Schema(description = "인버터 ID", example = "123")
    private Long inverterId;

    @Schema(description = "실시간 전력량 (kW)", example = "3.5")
    private Double pvPower;

    @Schema(description = "일일 누적 발전량 (kW)", example = "45.6")
    private Double cumulativeEnergy;

    private LocalDateTime timestamp;

    public InvertersDataResponseDto(Long inverterId, Double realtimeKw, Double dailyCumulativeKw, LocalDateTime timestamp) {
        this.inverterId = inverterId;
        this.pvPower = realtimeKw;
        this.cumulativeEnergy = dailyCumulativeKw;
        this.timestamp = timestamp;
    }
}
